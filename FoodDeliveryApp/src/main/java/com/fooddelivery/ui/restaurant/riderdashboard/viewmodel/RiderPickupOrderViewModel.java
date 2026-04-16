package com.fooddelivery.ui.restaurant.riderdashboard.viewmodel;

public class RiderPickupOrderViewModel {

    private final String orderId;
    private final String restaurantName;
    private final String deliveryAddressText;

    public RiderPickupOrderViewModel(String orderId,
                                     String restaurantName,
                                     String deliveryAddressText) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.deliveryAddressText = deliveryAddressText;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getDeliveryAddressText() {
        return deliveryAddressText;
    }
}