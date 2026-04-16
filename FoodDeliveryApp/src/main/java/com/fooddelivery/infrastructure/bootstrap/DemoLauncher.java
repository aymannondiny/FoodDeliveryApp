package com.fooddelivery.infrastructure.bootstrap;

import com.fooddelivery.application.auth.LoginUseCase;
import com.fooddelivery.application.auth.request.LoginCommand;
import com.fooddelivery.infrastructure.session.CurrentSession;
import com.fooddelivery.infrastructure.session.InMemoryCurrentSession;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Opens multiple dashboard windows simultaneously for demo/testing.
 * Each window gets its own isolated session to avoid cross-window conflicts.
 */
public class DemoLauncher {

    private final AppContext context;
    private final DashboardFactory dashboardFactory;

    public DemoLauncher(AppContext context, DashboardFactory dashboardFactory) {
        this.context = context;
        this.dashboardFactory = dashboardFactory;
    }

    public void launch() {
        openWindow(
                "rahim@example.com",
                "password123",
                "🍔 Customer – Rahim",
                new Point(50, 50)
        );

        openWindow(
                "kamal@example.com",
                "password123",
                "🍽 Restaurant Owner – Kamal",
                new Point(200, 100)
        );

        openWindow(
                "farhan@example.com",
                "password123",
                "🛵 Rider – Farhan",
                new Point(350, 150)
        );
    }

    private void openWindow(String email,
                            String password,
                            String title,
                            Point location) {
        try {
            CurrentSession isolatedSession = new InMemoryCurrentSession();

            LoginUseCase loginUseCase = new LoginUseCase(
                    context.userRepository(),
                    context.passwordHasher(),
                    isolatedSession
            );

            User user = loginUseCase.execute(new LoginCommand(email, password)).getUser();

            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setMinimumSize(new Dimension(480, 400));
            frame.setSize(1100, 740);
            frame.setLocation(location);

            JPanel dashboard = dashboardFactory.createIsolatedDashboard(
                    user,
                    frame::dispose,
                    isolatedSession
            );

            frame.setContentPane(dashboard);

            try {
                frame.setIconImage(new ImageIcon(
                        getClass().getResource("/icon.png")
                ).getImage());
            } catch (Exception ignored) {
            }

            frame.setVisible(true);

        } catch (Exception e) {
            System.err.println("Demo window failed for " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}