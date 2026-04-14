package com.fooddelivery;

import com.fooddelivery.api.ApiServer;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.LoginRegisterPanel;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.auth.AuthController;
import com.fooddelivery.ui.customer.CustomerDashboard;
import com.fooddelivery.ui.customer.cart.CartController;
import com.fooddelivery.ui.customer.menu.MenuController;
import com.fooddelivery.ui.customer.restaurants.RestaurantListController;
import com.fooddelivery.ui.restaurant.RestaurantDashboard;
import com.fooddelivery.ui.restaurant.RiderDashboard;
import com.fooddelivery.util.DataSeeder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Application entry point.
 *
 * Responsibilities:
 *  1. Seed demo data on first run
 *  2. Start the REST API server on port 8080
 *  3. Launch the Swing UI (auto-routes by user role after login)
 */
public class Main {

    private static JFrame frame;
    private static ApiServer apiServer;

    public static void main(String[] args) {
        UITheme.applyLookAndFeel();

        DataSeeder.seed();

        apiServer = new ApiServer();
        try {
            apiServer.start();
        } catch (IOException e) {
            System.err.println("Warning: Could not start API server – " + e.getMessage());
        }

        SwingUtilities.invokeLater(Main::showLoginScreen);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (apiServer != null) {
                apiServer.stop();
            }
        }));
    }

    static void showLoginScreen() {
        frame = buildFrame();

        AppContext context = AppContext.create();
        AuthController authController = new AuthController(
                context.registerUserUseCase(),
                context.loginUseCase(),
                context.logoutUseCase(),
                context.getCurrentUserUseCase()
        );

        LoginRegisterPanel loginPanel = new LoginRegisterPanel(
                authController,
                Main::onLoginSuccess
        );

        frame.setContentPane(loginPanel);
        frame.setSize(480, 460);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.setVisible(true);
    }

    static void onLoginSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            JPanel dashboard = buildDashboard(user);
            frame.setContentPane(dashboard);
            frame.setSize(1100, 740);
            frame.setLocationRelativeTo(null);
            frame.setTitle("🍔 Food Delivery  –  " + user.getName());
            frame.revalidate();
            frame.repaint();
        });
    }

    private static JPanel buildDashboard(User user) {
        return switch (user.getRole()) {
            case CUSTOMER -> new CustomerDashboard(
                    user,
                    Main::showLoginScreen,
                    createRestaurantListController(),
                    createMenuController(),
                    createCartController()
            );
            case RESTAURANT_OWNER -> new RestaurantDashboard(user, Main::showLoginScreen);
            case RIDER -> new RiderDashboard(user, Main::showLoginScreen);
            case ADMIN -> new CustomerDashboard(
                    user,
                    Main::showLoginScreen,
                    createRestaurantListController(),
                    createMenuController(),
                    createCartController()
            );
        };
    }

    private static RestaurantListController createRestaurantListController() {
        AppContext context = AppContext.create();
        return new RestaurantListController(context.restaurantQueryService());
    }

    private static MenuController createMenuController() {
        AppContext context = AppContext.create();
        return new MenuController(
                context.menuQueryService(),
                context.getCartUseCase(),
                context.addCartItemUseCase()
        );
    }

    private static CartController createCartController() {
        AppContext context = AppContext.create();
        return new CartController(
                context.getCartUseCase(),
                context.updateCartItemQuantityUseCase(),
                context.removeCartItemUseCase(),
                context.couponValidationUseCase(),
                context.getCurrentUserUseCase(),
                context.restaurantQueryService(),
                context.placeOrderUseCase()
        );
    }

    private static JFrame buildFrame() {
        if (frame != null) {
            frame.dispose();
        }

        JFrame f = new JFrame("🍔 Food Delivery App");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setMinimumSize(new Dimension(480, 400));

        try {
            f.setIconImage(new ImageIcon(
                    Main.class.getResource("/icon.png")
            ).getImage());
        } catch (Exception ignored) {
        }

        return f;
    }
}