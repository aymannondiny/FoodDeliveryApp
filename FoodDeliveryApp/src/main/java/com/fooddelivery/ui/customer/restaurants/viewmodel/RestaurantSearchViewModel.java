package com.fooddelivery.ui.customer.restaurants.viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantSearchViewModel {

    private final List<String> cuisineOptions;

    public RestaurantSearchViewModel(List<String> cuisineOptions) {
        this.cuisineOptions = cuisineOptions != null
                ? new ArrayList<>(cuisineOptions)
                : new ArrayList<>();
    }

    public List<String> getCuisineOptions() {
        return Collections.unmodifiableList(cuisineOptions);
    }
}