package com.fooddelivery.application.order.request;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;

public class PlaceOrderCommand {

    private final String customerId;
    private final Restaurant restaurant;
    private final Address deliveryAddress;
    private final Order.PaymentMethod paymentMethod;
    private final String couponCode;

    public PlaceOrderCommand(String customerId,
                             Restaurant restaurant,
                             Address deliveryAddress,
                             Order.PaymentMethod paymentMethod,
                             String couponCode) {
        this.customerId = customerId;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.couponCode = couponCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public Order.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }
}