package com.fooddelivery.ui.restaurant.dashboard.viewmodel;

import com.fooddelivery.model.OrderStatus;

public class RestaurantOrderViewModel {

    private final String orderId;
    private final String itemsSummary;
    private final String totalText;
    private final String statusDescription;
    private final OrderStatus currentStatus;
    private final OrderStatus nextStatus;
    private final String nextActionText;
    private final boolean cancellable;

    public RestaurantOrderViewModel(String orderId,
                                    String itemsSummary,
                                    String totalText,
                                    String statusDescription,
                                    OrderStatus currentStatus,
                                    OrderStatus nextStatus,
                                    String nextActionText,
                                    boolean cancellable) {
        this.orderId = orderId;
        this.itemsSummary = itemsSummary;
        this.totalText = totalText;
        this.statusDescription = statusDescription;
        this.currentStatus = currentStatus;
        this.nextStatus = nextStatus;
        this.nextActionText = nextActionText;
        this.cancellable = cancellable;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public String getTotalText() {
        return totalText;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getNextStatus() {
        return nextStatus;
    }

    public String getNextActionText() {
        return nextActionText;
    }

    public boolean isCancellable() {
        return cancellable;
    }
}