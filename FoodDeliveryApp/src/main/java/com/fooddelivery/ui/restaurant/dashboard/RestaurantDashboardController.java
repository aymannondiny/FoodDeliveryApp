package com.fooddelivery.ui.restaurant.dashboard;

import com.fooddelivery.application.auth.LogoutUseCase;
import com.fooddelivery.application.menu.MenuManagementService;
import com.fooddelivery.application.menu.MenuQueryService;
import com.fooddelivery.application.order.AdvanceOrderStatusUseCase;
import com.fooddelivery.application.order.CancelOrderUseCase;
import com.fooddelivery.application.order.CompleteDeliveryUseCase;
import com.fooddelivery.application.order.GetActiveRestaurantOrdersUseCase;
import com.fooddelivery.application.restaurant.RestaurantManagementService;
import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.application.restaurant.RestaurantRegistrationUseCase;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Schedule;
import com.fooddelivery.ui.restaurant.dashboard.request.MenuAddonForm;
import com.fooddelivery.ui.restaurant.dashboard.request.MenuItemForm;
import com.fooddelivery.ui.restaurant.dashboard.request.RestaurantRegistrationForm;
import com.fooddelivery.ui.restaurant.dashboard.request.RestaurantSettingsForm;
import com.fooddelivery.ui.restaurant.dashboard.viewmodel.RestaurantMenuRowViewModel;
import com.fooddelivery.ui.restaurant.dashboard.viewmodel.RestaurantOrderViewModel;
import com.fooddelivery.ui.restaurant.dashboard.viewmodel.RestaurantSettingsViewModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UI-facing controller for restaurant owner workflows.
 */
public class RestaurantDashboardController {

    private final LogoutUseCase logoutUseCase;
    private final RestaurantRegistrationUseCase restaurantRegistrationUseCase;
    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantManagementService restaurantManagementService;
    private final MenuQueryService menuQueryService;
    private final MenuManagementService menuManagementService;
    private final GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase;
    private final AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;
    private final com.fooddelivery.application.order.GetRestaurantOrdersUseCase getRestaurantOrdersUseCase;

    public RestaurantDashboardController(LogoutUseCase logoutUseCase,
                                         RestaurantRegistrationUseCase restaurantRegistrationUseCase,
                                         RestaurantQueryService restaurantQueryService,
                                         RestaurantManagementService restaurantManagementService,
                                         MenuQueryService menuQueryService,
                                         MenuManagementService menuManagementService,
                                         GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase,
                                         AdvanceOrderStatusUseCase advanceOrderStatusUseCase,
                                         CancelOrderUseCase cancelOrderUseCase,
                                         CompleteDeliveryUseCase completeDeliveryUseCase,
                                         com.fooddelivery.application.order.GetRestaurantOrdersUseCase getRestaurantOrdersUseCase) {
        this.logoutUseCase = logoutUseCase;
        this.restaurantRegistrationUseCase = restaurantRegistrationUseCase;
        this.restaurantQueryService = restaurantQueryService;
        this.restaurantManagementService = restaurantManagementService;
        this.menuQueryService = menuQueryService;
        this.menuManagementService = menuManagementService;
        this.getActiveRestaurantOrdersUseCase = getActiveRestaurantOrdersUseCase;
        this.advanceOrderStatusUseCase = advanceOrderStatusUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.completeDeliveryUseCase = completeDeliveryUseCase;
        this.getRestaurantOrdersUseCase = getRestaurantOrdersUseCase;
    }

    public void logout() {
        logoutUseCase.execute();
    }

    public Optional<Restaurant> findManagedRestaurant(String ownerId) {
        return restaurantQueryService.getByOwner(ownerId).stream().findFirst();
    }

    public Restaurant registerRestaurant(String ownerId, RestaurantRegistrationForm form) {
        double minOrder = parseDouble(form.getMinOrderText(), "Minimum order");
        int eta = parseInt(form.getEtaText(), "Estimated delivery time");

        Address address = new Address(
                form.getStreet(),
                form.getArea(),
                "Dhaka",
                ""
        );

        return restaurantRegistrationUseCase.execute(
                ownerId,
                form.getName(),
                form.getCuisineType(),
                address,
                form.getPhone(),
                15.0,
                minOrder,
                eta
        );
    }

    public Restaurant updateOpenStatus(Restaurant restaurant, boolean open) {
        restaurant.setOpen(open);
        restaurantManagementService.updateRestaurant(restaurant);
        return restaurant;
    }

    public List<RestaurantOrderViewModel> loadActiveOrders(String restaurantId) {
        return getActiveRestaurantOrdersUseCase.execute(restaurantId).stream()
                .map(this::toOrderViewModel)
                .collect(Collectors.toList());
    }

    public void advanceOrderToNextStatus(String orderId, OrderStatus currentStatus) {
        OrderStatus next = getNextStatus(currentStatus);
        if (next == null) {
            return;
        }

        if (next == OrderStatus.DELIVERED) {
            completeDeliveryUseCase.execute(orderId);
        } else {
            advanceOrderStatusUseCase.execute(orderId, next);
        }
    }

    public void cancelOrder(String orderId) {
        cancelOrderUseCase.execute(orderId);
    }

    public List<RestaurantMenuRowViewModel> loadMenuRows(String restaurantId) {
        return menuQueryService.getMenuForRestaurant(restaurantId).stream()
                .map(item -> new RestaurantMenuRowViewModel(
                        item.getId(),
                        item.getName(),
                        item.getCategory(),
                        String.format("%.2f", item.getPrice()),
                        item.isAvailable(),
                        item.getQuantity() == -1 ? "∞" : String.valueOf(item.getQuantity())
                ))
                .collect(Collectors.toList());
    }

    public MenuItem addMenuItem(String restaurantId, MenuItemForm form) {
        double price = parseDouble(form.getPriceText(), "Price");
        int stock = parseInt(form.getStockText(), "Stock");

        MenuItem item = menuManagementService.addMenuItem(
                restaurantId,
                form.getName(),
                form.getDescription(),
                form.getCategory(),
                price
        );

        if (stock != -1) {
            menuManagementService.updateQuantity(item.getId(), stock);
        }

        return item;
    }

    public void addAddon(String menuItemId, MenuAddonForm form) {
        double extraPrice = parseDouble(form.getExtraPriceText(), "Extra price");
        menuManagementService.addAddon(menuItemId, form.getName(), extraPrice);
    }

    public void toggleAvailability(String menuItemId, boolean currentlyAvailable) {
        menuManagementService.setAvailability(menuItemId, !currentlyAvailable);
    }

    public void deleteMenuItem(String menuItemId) {
        menuManagementService.deleteItem(menuItemId);
    }

    public RestaurantSettingsViewModel buildSettingsView(Restaurant restaurant) {
        String openTime = "09:00";
        String closeTime = "23:00";

        if (restaurant.getSchedule() != null) {
            if (restaurant.getSchedule().getOpenTime() != null) {
                openTime = restaurant.getSchedule().getOpenTime();
            }
            if (restaurant.getSchedule().getCloseTime() != null) {
                closeTime = restaurant.getSchedule().getCloseTime();
            }
        }

        int stars = (int) Math.round(restaurant.getRating());
        String ratingText;
        if (restaurant.getTotalRatings() == 0) {
            ratingText = "No ratings yet";
        } else {
            ratingText = "★".repeat(stars)
                    + "☆".repeat(5 - stars)
                    + String.format(" %.1f/5 (%d ratings)",
                    restaurant.getRating(),
                    restaurant.getTotalRatings());
        }

        return new RestaurantSettingsViewModel(
                restaurant.getName(),
                restaurant.getPhoneNumber(),
                String.valueOf((int) restaurant.getMinOrderAmount()),
                String.valueOf(restaurant.getEstimatedDeliveryMinutes()),
                openTime,
                closeTime,
                restaurant.getDescription() != null ? restaurant.getDescription() : "",
                ratingText,
                restaurant.getTotalRatings()
        );
    }

    public Restaurant saveSettings(Restaurant restaurant, RestaurantSettingsForm form) {
        restaurant.setName(form.getName());
        restaurant.setPhoneNumber(form.getPhone());
        restaurant.setDescription(form.getDescription());
        restaurant.setMinOrderAmount(parseDouble(form.getMinOrderText(), "Minimum order"));
        restaurant.setEstimatedDeliveryMinutes(parseInt(form.getEtaText(), "Estimated delivery time"));
        restaurant.setSchedule(Schedule.allDay(form.getOpenTime(), form.getCloseTime()));

        restaurantManagementService.updateRestaurant(restaurant);
        return restaurant;
    }

    public void updateStock(String menuItemId, int newQuantity) {
        if (newQuantity < -1) {
            throw new IllegalArgumentException("Stock must be -1 (unlimited) or a non-negative number.");
        }
        menuManagementService.updateQuantity(menuItemId, newQuantity);
    }

    public List<RestaurantOrderViewModel> loadAllOrders(String restaurantId) {
        return getRestaurantOrdersUseCase.execute(restaurantId).stream()
                .map(this::toOrderViewModel)
                .collect(Collectors.toList());
    }


    private RestaurantOrderViewModel toOrderViewModel(Order order) {
        OrderStatus next = getNextStatus(order.getStatus());

        String itemsSummary = order.getItems().stream()
                .map(i -> i.getQuantity() + "× " + i.getMenuItemName())
                .collect(Collectors.joining("  "));

        boolean rated = order.isRated();
        String foodRatingText = null;
        if (rated) {
            foodRatingText = "★".repeat((int) order.getRestaurantRating())
                    + "☆".repeat(5 - (int) order.getRestaurantRating())
                    + " (" + String.format("%.0f", order.getRestaurantRating()) + "/5)";
        }

        return new RestaurantOrderViewModel(
                order.getId(),
                itemsSummary,
                String.format("Total: %.2f BDT", order.getTotalAmount()),
                order.getStatus().getDescription(),
                order.getStatus(),
                next,
                next != null ? "→ " + next.name() : null,
                order.isCancellable(),
                rated,
                foodRatingText
        );
    }

    private OrderStatus getNextStatus(OrderStatus current) {
        return switch (current) {
            case PLACED -> OrderStatus.CONFIRMED;
            case CONFIRMED -> OrderStatus.PREPARING;
            case PREPARING -> OrderStatus.READY;
            case READY -> OrderStatus.PICKED_UP;
            case PICKED_UP -> OrderStatus.ON_THE_WAY;
            case ON_THE_WAY -> OrderStatus.DELIVERED;
            default -> null;
        };
    }

    private double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must be a valid integer.");
        }
    }
}