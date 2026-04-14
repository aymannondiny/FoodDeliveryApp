package com.fooddelivery.api.restaurant;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantService;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultRestaurantReader implements RestaurantReader {

    private final RestaurantService restaurantService;

    public DefaultRestaurantReader(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public List<Restaurant> findByCriteria(RestaurantSearchCriteria criteria) {
        if (criteria.hasArea()) {
            Address address = new Address("", criteria.getArea(), "Dhaka", "");
            return restaurantService.findNearby(address, 50);
        }

        if (criteria.hasCuisine()) {
            return restaurantService.filterByCuisine(criteria.getCuisine());
        }

        if (criteria.hasSearch()) {
            return restaurantService.search(criteria.getSearch());
        }

        // endpoint comment says "approved restaurants"
        return restaurantService.getAll().stream()
                .filter(Restaurant::isApproved)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCuisineTypes() {
        return restaurantService.getAllCuisineTypes();
    }
}