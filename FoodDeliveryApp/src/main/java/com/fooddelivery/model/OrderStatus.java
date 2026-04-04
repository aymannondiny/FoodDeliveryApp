package com.fooddelivery.model;

/**
 * Lifecycle states of a customer order.
 */
public enum OrderStatus {
    PLACED("Order placed – waiting for restaurant confirmation"),
    CONFIRMED("Restaurant confirmed your order"),
    PREPARING("Your food is being prepared"),
    READY("Food is ready – waiting for rider pickup"),
    PICKED_UP("Rider has picked up your order"),
    ON_THE_WAY("Your order is on the way"),
    DELIVERED("Order delivered successfully"),
    CANCELLED("Order was cancelled");

    private final String description;

    OrderStatus(String description) { this.description = description; }

    public String getDescription() { return description; }
}
