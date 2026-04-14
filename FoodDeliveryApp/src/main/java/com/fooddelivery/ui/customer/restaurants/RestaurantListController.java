package com.fooddelivery.ui.customer.restaurants;

import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.ui.customer.restaurants.viewmodel.RestaurantCardViewModel;
import com.fooddelivery.ui.customer.restaurants.viewmodel.RestaurantSearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UI-facing controller for restaurant discovery and filtering.
 */
public class RestaurantListController {

    private final RestaurantQueryService restaurantQueryService;

    public RestaurantListController(RestaurantQueryService restaurantQueryService) {
        this.restaurantQueryService = restaurantQueryService;
    }

    public RestaurantSearchViewModel loadSearchOptions() {
        List<String> cuisines = new ArrayList<>();
        cuisines.add("All Cuisines");
        cuisines.addAll(restaurantQueryService.getAllCuisineTypes());
        return new RestaurantSearchViewModel(cuisines);
    }

    public List<RestaurantCardViewModel> loadAllRestaurants() {
        List<Restaurant> restaurants = restaurantQueryService.getAll().stream()
                .filter(Restaurant::isApproved)
                .collect(Collectors.toList());

        return toCardViewModels(restaurants);
    }

    public List<RestaurantCardViewModel> search(String query, String cuisine) {
        String safeQuery = query != null ? query.trim() : "";
        String safeCuisine = cuisine != null ? cuisine.trim() : "";

        List<Restaurant> results;

        if (safeQuery.isEmpty() && (safeCuisine.isEmpty() || "All Cuisines".equalsIgnoreCase(safeCuisine))) {
            results = restaurantQueryService.getAll().stream()
                    .filter(Restaurant::isApproved)
                    .collect(Collectors.toList());
        } else if (!safeQuery.isEmpty()) {
            results = restaurantQueryService.search(safeQuery);
        } else {
            results = restaurantQueryService.filterByCuisine(safeCuisine);
        }

        return toCardViewModels(results);
    }

    private List<RestaurantCardViewModel> toCardViewModels(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(this::toCardViewModel)
                .collect(Collectors.toList());
    }

    private RestaurantCardViewModel toCardViewModel(Restaurant restaurant) {
        boolean open = restaurant.isCurrentlyOpen();

        String subtitle = restaurant.getCuisineType()
                + (restaurant.getDescription() != null && !restaurant.getDescription().isBlank()
                ? "  ·  " + restaurant.getDescription()
                : "");

        String ratingText = String.format(
                "★ %.1f  (%d ratings)",
                restaurant.getRating(),
                restaurant.getTotalRatings()
        );

        String statusText = open ? "● Open" : "● Closed";
        String minOrderText = "Min order: " + (int) restaurant.getMinOrderAmount() + " BDT";
        String deliveryText = "Delivery: " + restaurant.getEstimatedDeliveryMinutes() + " min";

        return new RestaurantCardViewModel(
                restaurant,
                restaurant.getName(),
                subtitle,
                ratingText,
                statusText,
                open,
                minOrderText,
                deliveryText
        );
    }
}