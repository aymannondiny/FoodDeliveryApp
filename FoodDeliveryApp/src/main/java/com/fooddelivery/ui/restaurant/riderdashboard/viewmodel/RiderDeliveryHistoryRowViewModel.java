package com.fooddelivery.ui.restaurant.riderdashboard.viewmodel;

public class RiderDeliveryHistoryRowViewModel {

    private final String orderId;
    private final String restaurantName;
    private final String totalAmountText;

    public RiderDeliveryHistoryRowViewModel(String orderId,
                                            String restaurantName,
                                            String totalAmountText) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.totalAmountText = totalAmountText;
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
}