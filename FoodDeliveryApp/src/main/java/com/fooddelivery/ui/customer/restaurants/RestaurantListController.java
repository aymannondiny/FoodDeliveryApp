package com.fooddelivery.ui.customer.restaurants;

import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Schedule;
import com.fooddelivery.ui.customer.restaurants.viewmodel.RestaurantCardViewModel;
import com.fooddelivery.ui.customer.restaurants.viewmodel.RestaurantSearchViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    /**
     * Computes a human-readable closure reason for a restaurant.
     * Returns null if the restaurant is currently open.
     */
    public String getClosureReason(Restaurant restaurant) {
        if (restaurant.isCurrentlyOpen()) {
            return null;
        }

        if (!restaurant.isApproved()) {
            return "Pending approval";
        }

        if (!restaurant.isOpen()) {
            return "Owner has paused orders";
        }

        Schedule schedule = restaurant.getSchedule();
        if (schedule == null) {
            return "Currently closed";
        }

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        Set<DayOfWeek> openDays = schedule.getOpenDays();

        if (openDays == null || openDays.isEmpty()) {
            return "No operating days configured";
        }

        if (!openDays.contains(today)) {
            DayOfWeek nextDay = findNextOpenDay(today, openDays);
            if (nextDay != null) {
                return "Closed today – Opens "
                        + formatDayName(nextDay)
                        + " at " + schedule.getOpenTime();
            }
            return "Closed today";
        }

        LocalTime now = LocalTime.now();

        try {
            LocalTime openTime = LocalTime.parse(schedule.getOpenTime());
            LocalTime closeTime = LocalTime.parse(schedule.getCloseTime());

            if (now.isBefore(openTime)) {
                return "Opens today at " + schedule.getOpenTime();
            }

            if (!now.isBefore(closeTime)) {
                DayOfWeek tomorrow = today.plus(1);
                if (openDays.contains(tomorrow)) {
                    return "Closed for today – Opens tomorrow at " + schedule.getOpenTime();
                }

                DayOfWeek nextDay = findNextOpenDay(today, openDays);
                if (nextDay != null) {
                    return "Closed – Opens "
                            + formatDayName(nextDay)
                            + " at " + schedule.getOpenTime();
                }
                return "Closed for today";
            }
        } catch (Exception e) {
            return "Currently closed";
        }

        return "Currently closed";
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

        String closureReason = getClosureReason(restaurant);

        String statusText;
        if (open) {
            statusText = "● Open";
        } else if (closureReason != null) {
            statusText = "● " + closureReason;
        } else {
            statusText = "● Closed";
        }

        String minOrderText = "Min order: " + (int) restaurant.getMinOrderAmount() + " BDT";
        String deliveryText = "Delivery: " + restaurant.getEstimatedDeliveryMinutes() + " min";

        return new RestaurantCardViewModel(
                restaurant,
                restaurant.getName(),
                subtitle,
                ratingText,
                statusText,
                open,
                closureReason,
                minOrderText,
                deliveryText
        );
    }

    private DayOfWeek findNextOpenDay(DayOfWeek today, Set<DayOfWeek> openDays) {
        DayOfWeek check = today.plus(1);
        for (int i = 0; i < 7; i++) {
            if (openDays.contains(check)) {
                return check;
            }
            check = check.plus(1);
        }
        return null;
    }

    private String formatDayName(DayOfWeek day) {
        String name = day.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}