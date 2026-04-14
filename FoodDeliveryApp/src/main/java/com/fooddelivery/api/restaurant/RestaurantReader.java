package com.fooddelivery.api.restaurant;

import com.fooddelivery.model.Restaurant;

import java.util.List;

public interface RestaurantReader {
    List<Restaurant> findByCriteria(RestaurantSearchCriteria criteria);
    List<String> getAllCuisineTypes();
}