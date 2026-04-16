package com.fooddelivery.application.restaurant;

import com.fooddelivery.domain.repository.RestaurantRepository;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Schedule;

public class RestaurantManagementService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantManagementService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public void setOpenStatus(String restaurantId, boolean open) {
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            restaurant.setOpen(open);
            restaurantRepository.save(restaurantId, restaurant);
        });
    }

    public void setSchedule(String restaurantId, Schedule schedule) {
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            restaurant.setSchedule(schedule);
            restaurantRepository.save(restaurantId, restaurant);
        });
    }

    public void updateRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant.getId(), restaurant);
    }

    public void addRating(String restaurantId, double score) {
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            restaurant.addRating(score);
            restaurantRepository.save(restaurantId, restaurant);
        });
    }
}