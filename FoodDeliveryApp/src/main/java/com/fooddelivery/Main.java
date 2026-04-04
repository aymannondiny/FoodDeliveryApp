package com.fooddelivery;

import com.fooddelivery.api.ApiServer;
import com.fooddelivery.model.User;
import com.fooddelivery.service.AuthService;
import com.fooddelivery.ui.LoginRegisterPanel;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.customer.CustomerDashboard;
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

    private static JFrame    frame;
    private static ApiServer apiServer;

    public static void main(String[] args) {
        // ── Apply look-and-feel before any Swing component is created ──────
        UITheme.applyLookAndFeel();

        // ── Seed sample data ───────────────────────────────────────────────
        DataSeeder.seed();

        // ── Start REST API server ──────────────────────────────────────────
        apiServer = new ApiServer();
        try {
            apiServer.start();
        } catch (IOException e) {
            System.err.println("Warning: Could not start API server – " + e.getMessage());
        }

        // ── Launch Swing window ────────────────────────────────────────────
        SwingUtilities.invokeLater(Main::showLoginScreen);

        // ── Shutdown hook ──────────────────────────────────────────────────
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (apiServer != null) apiServer.stop();
        }));
    }

    /** Shows the login/register panel in the main frame. */
    static void showLoginScreen() {
        frame = buildFrame();
        LoginRegisterPanel loginPanel = new LoginRegisterPanel(Main::onLoginSuccess);
        frame.setContentPane(loginPanel);
        frame.setSize(480, 460);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.setVisible(true);
    }

    /** Called by LoginRegisterPanel after successful authentication. */
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

    /** Route to the correct dashboard based on the user's role. */
    private static JPanel buildDashboard(User user) {
        return switch (user.getRole()) {
            case CUSTOMER         -> new CustomerDashboard(user, Main::showLoginScreen);
            case RESTAURANT_OWNER -> new RestaurantDashboard(user, Main::showLoginScreen);
            case RIDER            -> new RiderDashboard(user, Main::showLoginScreen);
            case ADMIN            -> new CustomerDashboard(user, Main::showLoginScreen); // Extend later
        };
    }

    private static JFrame buildFrame() {
        if (frame != null) { frame.dispose(); }
        JFrame f = new JFrame("🍔 Food Delivery App");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setMinimumSize(new Dimension(480, 400));
        try {
            f.setIconImage(new ImageIcon(
                Main.class.getResource("/icon.png")).getImage());
        } catch (Exception ignored) {}
        return f;
    }
}
