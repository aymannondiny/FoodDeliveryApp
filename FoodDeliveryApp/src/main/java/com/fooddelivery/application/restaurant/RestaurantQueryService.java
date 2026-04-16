package com.fooddelivery.application.restaurant;

import com.fooddelivery.domain.repository.RestaurantRepository;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantQueryService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> findNearby(Address deliveryAddress, double maxKm) {
        return restaurantRepository.findAll().stream()
                .filter(Restaurant::isApproved)
                .filter(r -> r.getAddress() != null
                        && r.getAddress().distanceTo(deliveryAddress) <= maxKm)
                .sorted(Comparator.comparingDouble(
                        r -> r.getAddress().distanceTo(deliveryAddress)))
                .collect(Collectors.toList());
    }

    public List<Restaurant> search(String query) {
        String q = query.toLowerCase();

        return restaurantRepository.findAll().stream()
                .filter(Restaurant::isApproved)
                .filter(r ->
                        (r.getName() != null && r.getName().toLowerCase().contains(q)) ||
                                (r.getCuisineType() != null && r.getCuisineType().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public List<Restaurant> filterByCuisine(String cuisine) {
        return restaurantRepository.findAll().stream()
                .filter(Restaurant::isApproved)
                .filter(r -> r.getCuisineType() != null
                        && r.getCuisineType().equalsIgnoreCase(cuisine))
                .collect(Collectors.toList());
    }

    public List<Restaurant> getTopRated(int limit) {
        return restaurantRepository.findAll().stream()
                .filter(Restaurant::isApproved)
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Restaurant> getByOwner(String ownerId) {
        return restaurantRepository.findByOwner(ownerId);
    }

    public Optional<Restaurant> findById(String id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public List<String> getAllCuisineTypes() {
        return restaurantRepository.findAll().stream()
                .filter(Restaurant::isApproved)
                .map(Restaurant::getCuisineType)
                .filter(cuisine -> cuisine != null && !cuisine.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}