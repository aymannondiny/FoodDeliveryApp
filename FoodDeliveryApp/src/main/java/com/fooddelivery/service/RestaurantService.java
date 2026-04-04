package com.fooddelivery.service;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * All business logic related to restaurants:
 * registration, search (by area / cuisine / rating), and status management.
 */
public class RestaurantService {

    private static RestaurantService instance;

    private RestaurantService() {}

    public static synchronized RestaurantService getInstance() {
        if (instance == null) instance = new RestaurantService();
        return instance;
    }

    // ── Registration ─────────────────────────────────────────────────────────

    /**
     * Register a new restaurant for an owner user.
     * The restaurant starts as unapproved (approved = false).
     */
    public Restaurant register(String ownerId, String name, String cuisineType,
                               Address address, String phone,
                               double deliveryFeePerKm, double minOrderAmount,
                               int estimatedDeliveryMinutes) {
        Restaurant r = new Restaurant(
            AppUtils.generateId("RST"), ownerId, name, cuisineType, address
        );
        r.setPhoneNumber(phone);
        r.setDeliveryFeePerKm(deliveryFeePerKm);
        r.setMinOrderAmount(minOrderAmount);
        r.setEstimatedDeliveryMinutes(estimatedDeliveryMinutes);
        r.setApproved(true);   // Auto-approve for demo purposes
        r.setOpen(true);
        RepositoryFactory.restaurants().save(r.getId(), r);
        return r;
    }

    // ── Discovery ────────────────────────────────────────────────────────────

    /** Returns all approved restaurants sorted by distance to the delivery address. */
    public List<Restaurant> findNearby(Address deliveryAddress, double maxKm) {
        return RepositoryFactory.restaurants().findAll().stream()
            .filter(Restaurant::isApproved)
            .filter(r -> r.getAddress() != null
                      && r.getAddress().distanceTo(deliveryAddress) <= maxKm)
            .sorted(Comparator.comparingDouble(
                r -> r.getAddress().distanceTo(deliveryAddress)))
            .collect(Collectors.toList());
    }

    /** Search by name or cuisine (case-insensitive substring). */
    public List<Restaurant> search(String query) {
        String q = query.toLowerCase();
        return RepositoryFactory.restaurants().findAll().stream()
            .filter(Restaurant::isApproved)
            .filter(r -> r.getName().toLowerCase().contains(q)
                      || r.getCuisineType().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    /** Filter approved restaurants by cuisine type. */
    public List<Restaurant> filterByCuisine(String cuisine) {
        return RepositoryFactory.restaurants().findAll().stream()
            .filter(Restaurant::isApproved)
            .filter(r -> r.getCuisineType().equalsIgnoreCase(cuisine))
            .collect(Collectors.toList());
    }

    /** Get restaurants sorted by rating (highest first). */
    public List<Restaurant> getTopRated(int limit) {
        return RepositoryFactory.restaurants().findAll().stream()
            .filter(Restaurant::isApproved)
            .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /** All restaurants belonging to a specific owner. */
    public List<Restaurant> getByOwner(String ownerId) {
        return RepositoryFactory.restaurants().findByOwner(ownerId);
    }

    public Optional<Restaurant> findById(String id) {
        return RepositoryFactory.restaurants().findById(id);
    }

    public List<Restaurant> getAll() {
        return RepositoryFactory.restaurants().findAll();
    }

    // ── Management (owner/admin) ──────────────────────────────────────────────

    public void setOpenStatus(String restaurantId, boolean open) {
        RepositoryFactory.restaurants().findById(restaurantId).ifPresent(r -> {
            r.setOpen(open);
            RepositoryFactory.restaurants().save(restaurantId, r);
        });
    }

    public void setSchedule(String restaurantId, Schedule schedule) {
        RepositoryFactory.restaurants().findById(restaurantId).ifPresent(r -> {
            r.setSchedule(schedule);
            RepositoryFactory.restaurants().save(restaurantId, r);
        });
    }

    public void updateRestaurant(Restaurant restaurant) {
        RepositoryFactory.restaurants().save(restaurant.getId(), restaurant);
    }

    public void addRating(String restaurantId, double score) {
        RepositoryFactory.restaurants().findById(restaurantId).ifPresent(r -> {
            r.addRating(score);
            RepositoryFactory.restaurants().save(restaurantId, r);
        });
    }

    /** Returns distinct cuisine types from all approved restaurants. */
    public List<String> getAllCuisineTypes() {
        return RepositoryFactory.restaurants().findAll().stream()
            .filter(Restaurant::isApproved)
            .map(Restaurant::getCuisineType)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
