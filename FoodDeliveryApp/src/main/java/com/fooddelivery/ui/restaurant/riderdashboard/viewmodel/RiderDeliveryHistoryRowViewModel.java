package com.fooddelivery.ui.restaurant.riderdashboard.viewmodel;

public class RiderDeliveryHistoryRowViewModel {

    private final String orderId;
    private final String restaurantName;
    private final String totalAmountText;
    private final boolean rated;
    private final String riderRatingText;

    public RiderDeliveryHistoryRowViewModel(String orderId,
                                            String restaurantName,
                                            String totalAmountText,
                                            boolean rated,
                                            String riderRatingText) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.totalAmountText = totalAmountText;
        this.rated = rated;
        this.riderRatingText = riderRatingText;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getTotalAmountText() {
        return totalAmountText;
    }

    public boolean isRated() {
        return rated;
    }

    public String getRiderRatingText() {
        return riderRatingText;
    }
}