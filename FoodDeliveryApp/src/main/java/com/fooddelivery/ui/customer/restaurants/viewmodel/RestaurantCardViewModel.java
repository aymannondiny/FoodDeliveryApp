package com.fooddelivery.ui.customer.restaurants.viewmodel;

import com.fooddelivery.model.Restaurant;

public class RestaurantCardViewModel {

    private final Restaurant restaurant;
    private final String name;
    private final String subtitle;
    private final String ratingText;
    private final String statusText;
    private final boolean open;
    private final String minOrderText;
    private final String deliveryText;

    public RestaurantCardViewModel(Restaurant restaurant,
                                   String name,
                                   String subtitle,
                                   String ratingText,
                                   String statusText,
                                   boolean open,
                                   String minOrderText,
                                   String deliveryText) {
        this.restaurant = restaurant;
        this.name = name;
        this.subtitle = subtitle;
        this.ratingText = ratingText;
        this.statusText = statusText;
        this.open = open;
        this.minOrderText = minOrderText;
        this.deliveryText = deliveryText;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getRatingText() {
        return ratingText;
    }

    public String getStatusText() {
        return statusText;
    }

    public boolean isOpen() {
        return open;
    }

    public String getMinOrderText() {
        return minOrderText;
    }

    public String getDeliveryText() {
        return deliveryText;
    }
}