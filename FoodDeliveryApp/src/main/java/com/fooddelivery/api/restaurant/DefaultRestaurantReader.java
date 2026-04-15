package com.fooddelivery.api.restaurant;

import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultRestaurantReader implements RestaurantReader {

    private final RestaurantQueryService restaurantQueryService;

    public DefaultRestaurantReader(RestaurantQueryService restaurantQueryService) {
        this.restaurantQueryService = restaurantQueryService;
    }

    @Override
    public List<Restaurant> findByCriteria(RestaurantSearchCriteria criteria) {
        if (criteria.hasArea()) {
            Address address = new Address("", criteria.getArea(), "Dhaka", "");
            return restaurantQueryService.findNearby(address, 50);
        }

        if (criteria.hasCuisine()) {
            return restaurantQueryService.filterByCuisine(criteria.getCuisine());
        }

        if (criteria.hasSearch()) {
            return restaurantQueryService.search(criteria.getSearch());
        }

        return restaurantQueryService.getAll().stream()
                .filter(Restaurant::isApproved)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCuisineTypes() {
        return restaurantQueryService.getAllCuisineTypes();
    }
}