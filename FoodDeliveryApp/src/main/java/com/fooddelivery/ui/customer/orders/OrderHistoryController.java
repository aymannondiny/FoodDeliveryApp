package com.fooddelivery.ui.customer.orders;

import com.fooddelivery.application.order.AdvanceOrderStatusUseCase;
import com.fooddelivery.application.order.CancelOrderUseCase;
import com.fooddelivery.application.order.CompleteDeliveryUseCase;
import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.application.order.GetOrderHistoryUseCase;
import com.fooddelivery.application.order.RateOrderUseCase;
import com.fooddelivery.application.payment.GetPaymentForOrderUseCase;
import com.fooddelivery.application.rider.FindRiderByIdUseCase;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.ui.customer.orders.viewmodel.OrderSummaryViewModel;
import com.fooddelivery.ui.customer.orders.viewmodel.TrackingStepViewModel;
import com.fooddelivery.ui.customer.orders.viewmodel.TrackingViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UI-facing controller for order history and tracking workflows.
 */
public class OrderHistoryController {

    private final GetOrderHistoryUseCase getOrderHistoryUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;
    private final FindRiderByIdUseCase findRiderByIdUseCase;
    private final GetPaymentForOrderUseCase getPaymentForOrderUseCase;
    private final RateOrderUseCase rateOrderUseCase;

    public OrderHistoryController(GetOrderHistoryUseCase getOrderHistoryUseCase,
                                  CancelOrderUseCase cancelOrderUseCase,
                                  GetOrderByIdUseCase getOrderByIdUseCase,
                                  AdvanceOrderStatusUseCase advanceOrderStatusUseCase,
                                  CompleteDeliveryUseCase completeDeliveryUseCase,
                                  FindRiderByIdUseCase findRiderByIdUseCase,
                                  GetPaymentForOrderUseCase getPaymentForOrderUseCase,
                                  RateOrderUseCase rateOrderUseCase) {
        this.getOrderHistoryUseCase = getOrderHistoryUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.advanceOrderStatusUseCase = advanceOrderStatusUseCase;
        this.completeDeliveryUseCase = completeDeliveryUseCase;
        this.findRiderByIdUseCase = findRiderByIdUseCase;
        this.getPaymentForOrderUseCase = getPaymentForOrderUseCase;
        this.rateOrderUseCase = rateOrderUseCase;
    }

    public List<OrderSummaryViewModel> loadOrderHistory(String customerId) {
        return getOrderHistoryUseCase.execute(customerId).stream()
                .map(this::toSummaryViewModel)
                .collect(Collectors.toList());
    }

    public void cancelOrder(String orderId) {
        cancelOrderUseCase.execute(orderId);
    }

    public void rateOrder(String orderId, double foodRating, double riderRating) {
        rateOrderUseCase.execute(orderId, foodRating, riderRating);
    }

    public TrackingViewModel loadTracking(String orderId) {
        Order order = getOrderByIdUseCase.execute(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        List<TrackingStepViewModel> steps = new ArrayList<>();
        for (OrderStatus step : OrderStatus.values()) {
            if (step == OrderStatus.CANCELLED) {
                continue;
            }

            boolean done = order.getStatusHistory() != null
                    && order.getStatusHistory().containsKey(step);

            boolean active = order.getStatus() == step;

            String timeText = null;
            if (done) {
                LocalDateTime time = order.getStatusHistory().get(step);
                if (time != null) {
                    String raw = time.toLocalTime().toString();
                    timeText = raw.length() > 8 ? raw.substring(0, 8) : raw;
                }
            }

            steps.add(new TrackingStepViewModel(
                    step.getDescription(),
                    done,
                    active,
                    timeText
            ));
        }

        String riderText = null;
        if (order.getRiderId() != null) {
            riderText = findRiderByIdUseCase.execute(order.getRiderId())
                    .map(r -> "🛵  Rider: " + r.getName() + "  ·  " + r.getPhone())
                    .orElse(null);
        }

        String paymentText = getPaymentForOrderUseCase.execute(order.getId())
                .map(p -> "Payment: " + p.getMethod().name() + "  [" + p.getStatus().name() + "]")
                .orElse(null);

        boolean canAdvanceDemo = order.getStatus() != OrderStatus.DELIVERED
                && order.getStatus() != OrderStatus.CANCELLED;

        return new TrackingViewModel(
                order.getId(),
                steps,
                riderText,
                paymentText,
                canAdvanceDemo
        );
    }

    public String advanceDemoStatus(String orderId) {
        Order order = getOrderByIdUseCase.execute(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus current = order.getStatus();
        if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order can no longer advance.");
        }

        OrderStatus next = OrderStatus.values()[current.ordinal() + 1];

        if (next == OrderStatus.DELIVERED) {
            completeDeliveryUseCase.execute(orderId);
        } else {
            advanceOrderStatusUseCase.execute(orderId, next);
        }

        return next.name();
    }

    private OrderSummaryViewModel toSummaryViewModel(Order order) {
        String dateText = order.getCreatedAt() != null
                ? order.getCreatedAt().toLocalDate().toString()
                : "";

        String itemsSummary = order.getItems() == null
                ? ""
                : order.getItems().stream()
                .map(this::toItemSummary)
                .collect(Collectors.joining("  "));

        boolean rateable = order.getStatus() == OrderStatus.DELIVERED && !order.isRated();
        boolean rated = order.isRated();

        return new OrderSummaryViewModel(
                order.getId(),
                order.getRestaurantName(),
                dateText,
                itemsSummary,
                order.getStatus(),
                order.getStatus().name(),
                String.format("%.2f BDT", order.getTotalAmount()),
                order.isCancellable(),
                rateable,
                rated
        );
    }

    private String toItemSummary(OrderItem item) {
        return item.getQuantity() + "× " + item.getMenuItemName();
    }
}