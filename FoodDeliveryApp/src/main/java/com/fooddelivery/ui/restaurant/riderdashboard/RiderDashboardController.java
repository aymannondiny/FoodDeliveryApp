package com.fooddelivery.ui.restaurant.riderdashboard;

import com.fooddelivery.application.auth.LogoutUseCase;
import com.fooddelivery.application.order.AcceptPickupUseCase;
import com.fooddelivery.application.order.AdvanceOrderStatusUseCase;
import com.fooddelivery.application.order.CompleteDeliveryUseCase;
import com.fooddelivery.application.order.GetDeliveredOrdersForRiderUseCase;
import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.application.order.GetReadyForPickupOrdersUseCase;
import com.fooddelivery.application.rider.FindRiderByIdUseCase;
import com.fooddelivery.application.rider.FindRiderByUserIdUseCase;
import com.fooddelivery.application.rider.RegisterRiderUseCase;
import com.fooddelivery.application.rider.SetRiderAvailabilityUseCase;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.Rider;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderCurrentOrderViewModel;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderDeliveryHistoryRowViewModel;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderPickupOrderViewModel;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderStatsViewModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UI-facing controller for rider workflows.
 */
public class RiderDashboardController {

    private final LogoutUseCase logoutUseCase;
    private final FindRiderByUserIdUseCase findRiderByUserIdUseCase;
    private final RegisterRiderUseCase registerRiderUseCase;
    private final FindRiderByIdUseCase findRiderByIdUseCase;
    private final SetRiderAvailabilityUseCase setRiderAvailabilityUseCase;
    private final GetReadyForPickupOrdersUseCase getReadyForPickupOrdersUseCase;
    private final AcceptPickupUseCase acceptPickupUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;
    private final GetDeliveredOrdersForRiderUseCase getDeliveredOrdersForRiderUseCase;

    public RiderDashboardController(LogoutUseCase logoutUseCase,
                                    FindRiderByUserIdUseCase findRiderByUserIdUseCase,
                                    RegisterRiderUseCase registerRiderUseCase,
                                    FindRiderByIdUseCase findRiderByIdUseCase,
                                    SetRiderAvailabilityUseCase setRiderAvailabilityUseCase,
                                    GetReadyForPickupOrdersUseCase getReadyForPickupOrdersUseCase,
                                    AcceptPickupUseCase acceptPickupUseCase,
                                    GetOrderByIdUseCase getOrderByIdUseCase,
                                    AdvanceOrderStatusUseCase advanceOrderStatusUseCase,
                                    CompleteDeliveryUseCase completeDeliveryUseCase,
                                    GetDeliveredOrdersForRiderUseCase getDeliveredOrdersForRiderUseCase) {
        this.logoutUseCase = logoutUseCase;
        this.findRiderByUserIdUseCase = findRiderByUserIdUseCase;
        this.registerRiderUseCase = registerRiderUseCase;
        this.findRiderByIdUseCase = findRiderByIdUseCase;
        this.setRiderAvailabilityUseCase = setRiderAvailabilityUseCase;
        this.getReadyForPickupOrdersUseCase = getReadyForPickupOrdersUseCase;
        this.acceptPickupUseCase = acceptPickupUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.advanceOrderStatusUseCase = advanceOrderStatusUseCase;
        this.completeDeliveryUseCase = completeDeliveryUseCase;
        this.getDeliveredOrdersForRiderUseCase = getDeliveredOrdersForRiderUseCase;
    }

    public void logout() {
        logoutUseCase.execute();
    }

    public Rider ensureRiderProfile(User riderUser) {
        return findRiderByUserIdUseCase.execute(riderUser.getId())
                .orElseGet(() -> registerRiderUseCase.execute(
                        riderUser.getId(),
                        riderUser.getName(),
                        riderUser.getPhone(),
                        "Bike"
                ));
    }

    public Rider reloadRider(String riderId) {
        return findRiderByIdUseCase.execute(riderId)
                .orElseThrow(() -> new IllegalStateException("Rider profile not found."));
    }

    public Rider updateAvailability(String riderId, boolean available) {
        return setRiderAvailabilityUseCase.execute(riderId, available);
    }

    public List<RiderPickupOrderViewModel> loadReadyForPickupOrders() {
        return getReadyForPickupOrdersUseCase.execute().stream()
                .map(order -> new RiderPickupOrderViewModel(
                        order.getId(),
                        order.getRestaurantName(),
                        String.valueOf(order.getDeliveryAddress())
                ))
                .collect(Collectors.toList());
    }

    public RiderCurrentOrderViewModel loadCurrentAssignment(String riderId) {
        Rider rider = reloadRider(riderId);
        if (rider.getCurrentOrderId() == null) {
            return null;
        }

        Order order = getOrderByIdUseCase.execute(rider.getCurrentOrderId())
                .orElseThrow(() -> new IllegalStateException("Assigned order not found."));

        return new RiderCurrentOrderViewModel(
                order.getId(),
                order.getRestaurantName(),
                String.valueOf(order.getDeliveryAddress()),
                String.format("%.2f BDT", order.getTotalAmount()),
                order.getPaymentMethod().name(),
                order.getStatus(),
                order.getStatus() == OrderStatus.PICKED_UP,
                order.getStatus() == OrderStatus.ON_THE_WAY
        );
    }

    public void acceptPickup(String riderId, String orderId) {
        acceptPickupUseCase.execute(riderId, orderId);
    }

    public void markOnTheWay(String orderId) {
        advanceOrderStatusUseCase.execute(orderId, OrderStatus.ON_THE_WAY);
    }

    public void markDelivered(String orderId) {
        completeDeliveryUseCase.execute(orderId);
    }

    public List<RiderDeliveryHistoryRowViewModel> loadDeliveryHistory(String riderId) {
        return getDeliveredOrdersForRiderUseCase.execute(riderId).stream()
                .map(order -> new RiderDeliveryHistoryRowViewModel(
                        order.getId(),
                        order.getRestaurantName(),
                        String.format("%.2f BDT", order.getTotalAmount())
                ))
                .collect(Collectors.toList());
    }

    public RiderStatsViewModel loadStats(String riderId) {
        Rider rider = reloadRider(riderId);
        List<Order> delivered = getDeliveredOrdersForRiderUseCase.execute(riderId);

        long totalDeliveries = delivered.size();
        double totalEarnings = delivered.stream()
                .mapToDouble(Order::getDeliveryFee)
                .sum();

        String statusText = rider.getCurrentOrderId() != null
                ? "Busy"
                : (rider.isAvailable() ? "Available" : "Unavailable");

        return new RiderStatsViewModel(
                String.valueOf(totalDeliveries),
                String.format("%.2f BDT", totalEarnings),
                rider.getVehicleType(),
                statusText
        );
    }
}