package com.fooddelivery.ui.customer.orders.viewmodel;

import com.fooddelivery.model.OrderStatus;

public class OrderSummaryViewModel {

    private final String orderId;
    private final String restaurantName;
    private final String dateText;
    private final String itemsSummaryText;
    private final OrderStatus status;
    private final String statusText;
    private final String totalText;
    private final boolean cancellable;

    public OrderSummaryViewModel(String orderId,
                                 String restaurantName,
                                 String dateText,
                                 String itemsSummaryText,
                                 OrderStatus status,
                                 String statusText,
                                 String totalText,
                                 boolean cancellable) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.dateText = dateText;
        this.itemsSummaryText = itemsSummaryText;
        this.status = status;
        this.statusText = statusText;
        this.totalText = totalText;
        this.cancellable = cancellable;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getDateText() {
        return dateText;
    }

    public String getItemsSummaryText() {
        return itemsSummaryText;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getTotalText() {
        return totalText;
    }

    public boolean isCancellable() {
        return cancellable;
    }
}