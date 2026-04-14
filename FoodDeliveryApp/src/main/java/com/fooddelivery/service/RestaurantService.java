package com.fooddelivery.service;

import com.fooddelivery.application.restaurant.RestaurantManagementService;
import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.application.restaurant.RestaurantRegistrationUseCase;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Schedule;

import java.util.List;
import java.util.Optional;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer restaurant application services from AppContext.
 */
public class RestaurantService {

    private static RestaurantService instance;

    private final RestaurantRegistrationUseCase registrationUseCase;
    private final RestaurantQueryService queryService;
    private final RestaurantManagementService managementService;

    private RestaurantService() {
        AppContext context = AppContext.create();
        this.registrationUseCase = context.restaurantRegistrationUseCase();
        this.queryService = context.restaurantQueryService();
        this.managementService = context.restaurantManagementService();
    }

    public static synchronized RestaurantService getInstance() {
        if (instance == null) {
            instance = new RestaurantService();
        }
        return instance;
    }

    public Restaurant register(String ownerId, String name, String cuisineType,
                               Address address, String phone,
                               double deliveryFeePerKm, double minOrderAmount,
                               int estimatedDeliveryMinutes) {
        return registrationUseCase.execute(
                ownerId,
                name,
                cuisineType,
                address,
                phone,
                deliveryFeePerKm,
                minOrderAmount,
                estimatedDeliveryMinutes
        );
    }

    public List<Restaurant> findNearby(Address deliveryAddress, double maxKm) {
        return queryService.findNearby(deliveryAddress, maxKm);
    }

    public List<Restaurant> search(String query) {
        return queryService.search(query);
    }

    public List<Restaurant> filterByCuisine(String cuisine) {
        return queryService.filterByCuisine(cuisine);
    }

    public List<Restaurant> getTopRated(int limit) {
        return queryService.getTopRated(limit);
    }

    public List<Restaurant> getByOwner(String ownerId) {
        return queryService.getByOwner(ownerId);
    }

    public Optional<Restaurant> findById(String id) {
        return queryService.findById(id);
    }

    public List<Restaurant> getAll() {
        return queryService.getAll();
    }

    public void setOpenStatus(String restaurantId, boolean open) {
        managementService.setOpenStatus(restaurantId, open);
    }

    public void setSchedule(String restaurantId, Schedule schedule) {
        managementService.setSchedule(restaurantId, schedule);
    }

    public void updateRestaurant(Restaurant restaurant) {
        managementService.updateRestaurant(restaurant);
    }

    public void addRating(String restaurantId, double score) {
        managementService.addRating(restaurantId, score);
    }

    public List<String> getAllCuisineTypes() {
        return queryService.getAllCuisineTypes();
    }
}