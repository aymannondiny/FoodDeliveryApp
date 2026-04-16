package com.fooddelivery.infrastructure.bootstrap;

import com.fooddelivery.model.User;
import com.fooddelivery.ui.auth.AuthController;
import com.fooddelivery.ui.customer.CustomerDashboard;
import com.fooddelivery.ui.customer.cart.CartController;
import com.fooddelivery.ui.customer.menu.MenuController;
import com.fooddelivery.ui.customer.orders.OrderHistoryController;
import com.fooddelivery.ui.customer.restaurants.RestaurantListController;
import com.fooddelivery.ui.restaurant.RestaurantDashboard;
import com.fooddelivery.ui.restaurant.RiderDashboard;
import com.fooddelivery.ui.restaurant.dashboard.RestaurantDashboardController;
import com.fooddelivery.ui.restaurant.riderdashboard.RiderDashboardController;

import javax.swing.*;

/**
 * Creates UI controllers and dashboards from AppContext.
 * Keeps Main focused on startup only.
 */
public class DashboardFactory {

    private final AppContext context;

    public DashboardFactory(AppContext context) {
        this.context = context;
    }

    public AuthController createAuthController() {
        return new AuthController(
                context.registerUserUseCase(),
                context.loginUseCase(),
                context.logoutUseCase(),
                context.getCurrentUserUseCase()
        );
    }

    public JPanel createDashboard(User user, Runnable onLogout) {
        return switch (user.getRole()) {
            case CUSTOMER -> new CustomerDashboard(
                    user,
                    onLogout,
                    createRestaurantListController(),
                    createMenuController(),
                    createCartController(),
                    createOrderHistoryController()
            );
            case RESTAURANT_OWNER -> new RestaurantDashboard(
                    user,
                    onLogout,
                    createRestaurantDashboardController()
            );
            case RIDER -> new RiderDashboard(
                    user,
                    onLogout,
                    createRiderDashboardController()
            );
            case ADMIN -> new CustomerDashboard(
                    user,
                    onLogout,
                    createRestaurantListController(),
                    createMenuController(),
                    createCartController(),
                    createOrderHistoryController()
            );
        };
    }

    private RestaurantListController createRestaurantListController() {
        return new RestaurantListController(context.restaurantQueryService());
    }

    private MenuController createMenuController() {
        return new MenuController(
                context.menuQueryService(),
                context.getCartUseCase(),
                context.addCartItemUseCase()
        );
    }

    private CartController createCartController() {
        return new CartController(
                context.getCartUseCase(),
                context.updateCartItemQuantityUseCase(),
                context.removeCartItemUseCase(),
                context.clearCartUseCase(),
                context.couponValidationUseCase(),
                context.getCurrentUserUseCase(),
                context.restaurantQueryService(),
                context.placeOrderUseCase()
        );
    }

    private OrderHistoryController createOrderHistoryController() {
        return new OrderHistoryController(
                context.getOrderHistoryUseCase(),
                context.cancelOrderUseCase(),
                context.getOrderByIdUseCase(),
                context.advanceOrderStatusUseCase(),
                context.completeDeliveryUseCase(),
                context.findRiderByIdUseCase(),
                context.getPaymentForOrderUseCase()
        );
    }

    private RestaurantDashboardController createRestaurantDashboardController() {
        return new RestaurantDashboardController(
                context.logoutUseCase(),
                context.restaurantRegistrationUseCase(),
                context.restaurantQueryService(),
                context.restaurantManagementService(),
                context.menuQueryService(),
                context.menuManagementService(),
                context.getActiveRestaurantOrdersUseCase(),
                context.advanceOrderStatusUseCase(),
                context.cancelOrderUseCase(),
                context.completeDeliveryUseCase()
        );
    }

    private RiderDashboardController createRiderDashboardController() {
        return new RiderDashboardController(
                context.logoutUseCase(),
                context.findRiderByUserIdUseCase(),
                context.registerRiderUseCase(),
                context.findRiderByIdUseCase(),
                context.setRiderAvailabilityUseCase(),
                context.getReadyForPickupOrdersUseCase(),
                context.acceptPickupUseCase(),
                context.getOrderByIdUseCase(),
                context.advanceOrderStatusUseCase(),
                context.completeDeliveryUseCase(),
                context.getDeliveredOrdersForRiderUseCase()
        );
    }
}