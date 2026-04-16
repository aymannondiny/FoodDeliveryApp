package com.fooddelivery.service;

import com.fooddelivery.application.order.AdvanceOrderStatusUseCase;
import com.fooddelivery.application.order.CancelOrderUseCase;
import com.fooddelivery.application.order.CompleteDeliveryUseCase;
import com.fooddelivery.application.order.GetActiveRestaurantOrdersUseCase;
import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.application.order.GetOrderHistoryUseCase;
import com.fooddelivery.application.order.GetRestaurantOrdersUseCase;
import com.fooddelivery.application.order.PlaceOrderUseCase;
import com.fooddelivery.application.order.request.PlaceOrderCommand;
import com.fooddelivery.domain.service.RiderAssigner;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.Restaurant;

import java.util.List;
import java.util.Optional;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer order use cases from AppContext.
 */
@Deprecated
public class OrderService {

    private static OrderService instance;

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetOrderHistoryUseCase getOrderHistoryUseCase;
    private final GetRestaurantOrdersUseCase getRestaurantOrdersUseCase;
    private final GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase;
    private final AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;
    private final RiderAssigner riderAssigner;

    private OrderService() {
        AppContext context = AppContext.create();
        this.placeOrderUseCase = context.placeOrderUseCase();
        this.getOrderByIdUseCase = context.getOrderByIdUseCase();
        this.getOrderHistoryUseCase = context.getOrderHistoryUseCase();
        this.getRestaurantOrdersUseCase = context.getRestaurantOrdersUseCase();
        this.getActiveRestaurantOrdersUseCase = context.getActiveRestaurantOrdersUseCase();
        this.advanceOrderStatusUseCase = context.advanceOrderStatusUseCase();
        this.cancelOrderUseCase = context.cancelOrderUseCase();
        this.completeDeliveryUseCase = context.completeDeliveryUseCase();
        this.riderAssigner = context.riderAssigner();
    }

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public Order placeOrder(String customerId,
                            Restaurant restaurant,
                            Address deliveryAddress,
                            Order.PaymentMethod paymentMethod,
                            String couponCode) {
        return placeOrderUseCase.execute(
                new PlaceOrderCommand(
                        customerId,
                        restaurant,
                        deliveryAddress,
                        paymentMethod,
                        couponCode
                )
        );
    }

    public Order advanceStatus(String orderId, OrderStatus newStatus) {
        return advanceOrderStatusUseCase.execute(orderId, newStatus);
    }

    public Order cancelOrder(String orderId) {
        return cancelOrderUseCase.execute(orderId);
    }

    public void assignRider(Order order) {
        riderAssigner.assignTo(order);
    }

    public void completeDelivery(String orderId) {
        completeDeliveryUseCase.execute(orderId);
    }

    public Optional<Order> findById(String id) {
        return getOrderByIdUseCase.execute(id);
    }

    public List<Order> getOrderHistory(String customerId) {
        return getOrderHistoryUseCase.execute(customerId);
    }

    public List<Order> getRestaurantOrders(String restaurantId) {
        return getRestaurantOrdersUseCase.execute(restaurantId);
    }

    public List<Order> getActiveRestaurantOrders(String restaurantId) {
        return getActiveRestaurantOrdersUseCase.execute(restaurantId);
    }
}