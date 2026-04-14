package com.fooddelivery.ui.customer.cart.viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartViewModel {

    private final boolean empty;
    private final String restaurantName;
    private final List<CartItemViewModel> items;
    private final double subtotal;
    private final int totalItems;

    public CartViewModel(boolean empty,
                         String restaurantName,
                         List<CartItemViewModel> items,
                         double subtotal,
                         int totalItems) {
        this.empty = empty;
        this.restaurantName = restaurantName;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.subtotal = subtotal;
        this.totalItems = totalItems;
    }

    public boolean isEmpty() {
        return empty;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public List<CartItemViewModel> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getSubtotal() {
        return subtotal;
    }

    public int getTotalItems() {
        return totalItems;
    }
}