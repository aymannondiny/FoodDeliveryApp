package com.fooddelivery;

import com.fooddelivery.api.ApiServer;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.infrastructure.bootstrap.AppSeeder;
import com.fooddelivery.infrastructure.bootstrap.DashboardFactory;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.LoginRegisterPanel;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Application entry point.
 */
public class Main {

    private static JFrame frame;
    private static ApiServer apiServer;
    private static AppContext context;
    private static DashboardFactory dashboardFactory;

    public static void main(String[] args) {
        UITheme.applyLookAndFeel();

        context = AppContext.create();
        dashboardFactory = new DashboardFactory(context);

        new AppSeeder(context).seedIfNeeded();

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

        LoginRegisterPanel loginPanel = new LoginRegisterPanel(
                dashboardFactory.createAuthController(),
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
            JPanel dashboard = dashboardFactory.createDashboard(user, Main::showLoginScreen);
            frame.setContentPane(dashboard);
            frame.setSize(1100, 740);
            frame.setLocationRelativeTo(null);
            frame.setTitle("🍔 Food Delivery  –  " + user.getName());
            frame.revalidate();
            frame.repaint();
        });
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