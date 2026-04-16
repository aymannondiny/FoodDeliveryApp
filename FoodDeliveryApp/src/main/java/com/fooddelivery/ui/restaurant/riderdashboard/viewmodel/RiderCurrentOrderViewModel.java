package com.fooddelivery.ui.restaurant.riderdashboard.viewmodel;

import com.fooddelivery.model.OrderStatus;

public class RiderCurrentOrderViewModel {

    private final String orderId;
    private final String restaurantName;
    private final String deliveryAddressText;
    private final String amountText;
    private final String paymentText;
    private final OrderStatus status;
    private final boolean canMarkOnTheWay;
    private final boolean canMarkDelivered;

    public RiderCurrentOrderViewModel(String orderId,
                                      String restaurantName,
                                      String deliveryAddressText,
                                      String amountText,
                                      String paymentText,
                                      OrderStatus status,
                                      boolean canMarkOnTheWay,
                                      boolean canMarkDelivered) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.deliveryAddressText = deliveryAddressText;
        this.amountText = amountText;
        this.paymentText = paymentText;
        this.status = status;
        this.canMarkOnTheWay = canMarkOnTheWay;
        this.canMarkDelivered = canMarkDelivered;
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

    public String getAmountText() {
        return amountText;
    }

    public String getPaymentText() {
        return paymentText;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isCanMarkOnTheWay() {
        return canMarkOnTheWay;
    }

    public boolean isCanMarkDelivered() {
        return canMarkDelivered;
    }
}