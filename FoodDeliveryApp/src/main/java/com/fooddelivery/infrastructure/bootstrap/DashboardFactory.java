package com.fooddelivery.infrastructure.bootstrap;

import com.fooddelivery.application.auth.GetCurrentUserUseCase;
import com.fooddelivery.application.auth.LoginUseCase;
import com.fooddelivery.application.auth.LogoutUseCase;
import com.fooddelivery.application.cart.AddCartItemUseCase;
import com.fooddelivery.application.cart.ClearCartUseCase;
import com.fooddelivery.application.cart.GetCartUseCase;
import com.fooddelivery.application.cart.RemoveCartItemUseCase;
import com.fooddelivery.application.cart.UpdateCartItemQuantityUseCase;
import com.fooddelivery.infrastructure.repository.memory.InMemoryCartRepository;
import com.fooddelivery.infrastructure.session.CurrentSession;
import com.fooddelivery.infrastructure.session.InMemoryCurrentSession;
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
 * Supports both shared-session (normal) and isolated-session (demo) modes.
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

    /**
     * Normal mode: uses the shared session from AppContext.
     */
    public JPanel createDashboard(User user, Runnable onLogout) {
        return buildDashboard(user, onLogout, context.currentSession());
    }

    /**
     * Demo mode: uses a dedicated session per window.
     */
    public JPanel createIsolatedDashboard(User user, Runnable onLogout, CurrentSession isolatedSession) {
        return buildDashboard(user, onLogout, isolatedSession);
    }

    private JPanel buildDashboard(User user, Runnable onLogout, CurrentSession session) {
        return switch (user.getRole()) {
            case CUSTOMER -> new CustomerDashboard(
                    user,
                    onLogout,
                    createRestaurantListController(),
                    createMenuController(session),
                    createCartController(session),
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
                    createMenuController(session),
                    createCartController(session),
                    createOrderHistoryController()
            );
        };
    }

    private RestaurantListController createRestaurantListController() {
        return new RestaurantListController(context.restaurantQueryService());
    }

    private MenuController createMenuController(CurrentSession session) {
        return new MenuController(
                context.menuQueryService(),
                new GetCartUseCase(context.cartRepository(), session),
                new AddCartItemUseCase(context.cartRepository(), session)
        );
    }

    private CartController createCartController(CurrentSession session) {
        return new CartController(
                new GetCartUseCase(context.cartRepository(), session),
                new UpdateCartItemQuantityUseCase(context.cartRepository(), session),
                new RemoveCartItemUseCase(context.cartRepository(), session),
                new ClearCartUseCase(context.cartRepository(), session),
                context.couponValidationUseCase(),
                new GetCurrentUserUseCase(session),
                context.restaurantQueryService(),
                context.placeOrderUseCase(),
                context.menuQueryService()
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