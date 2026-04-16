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
import com.fooddelivery.domain.repository.RiderRepository;

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
    private final RiderRepository riderRepository;

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
                                    GetDeliveredOrdersForRiderUseCase getDeliveredOrdersForRiderUseCase,
                                    RiderRepository riderRepository) {
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
        this.riderRepository = riderRepository;
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
                .map(order -> {
                    boolean rated = order.isRated();
                    String riderRatingText = null;
                    if (rated && order.getRiderRating() > 0) {
                        int stars = (int) order.getRiderRating();
                        riderRatingText = "★".repeat(stars)
                                + "☆".repeat(5 - stars)
                                + " (" + String.format("%.0f", order.getRiderRating()) + "/5)";
                    }

                    return new RiderDeliveryHistoryRowViewModel(
                            order.getId(),
                            order.getRestaurantName(),
                            String.format("%.2f BDT", order.getTotalAmount()),
                            rated,
                            riderRatingText
                    );
                })
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

        // Compute rating from actual order data for accuracy
        List<Order> ratedOrders = delivered.stream()
                .filter(Order::isRated)
                .filter(o -> o.getRiderRating() > 0)
                .collect(Collectors.toList());

        long ratedCount = ratedOrders.size();

        String averageRatingText;
        if (ratedCount == 0) {
            averageRatingText = "No ratings yet";
        } else {
            double avgRating = ratedOrders.stream()
                    .mapToDouble(Order::getRiderRating)
                    .average()
                    .orElse(0);

            int stars = (int) Math.round(avgRating);
            averageRatingText = "★".repeat(stars)
                    + "☆".repeat(5 - stars)
                    + String.format(" %.1f/5 (%d ratings)", avgRating, ratedCount);

            // Sync rider model if out of date
            if (rider.getTotalRatings() != ratedCount) {
                rider.setRating(avgRating);
                rider.setTotalRatings((int) ratedCount);
                riderRepository.save(rider.getId(), rider);
            }
        }

        return new RiderStatsViewModel(
                String.valueOf(totalDeliveries),
                String.format("%.2f BDT", totalEarnings),
                rider.getVehicleType(),
                statusText,
                averageRatingText
        );
    }
}