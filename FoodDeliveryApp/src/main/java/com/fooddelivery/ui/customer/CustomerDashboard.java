package com.fooddelivery.ui.customer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.service.AuthService;
import com.fooddelivery.service.CartService;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Top-level container for the customer experience.
 * Manages navigation between: Restaurants → Menu → Cart → Orders.
 */
public class CustomerDashboard extends JPanel {

    private final User    currentUser;
    private final Runnable onLogout;

    private JTabbedPane   tabs;
    private CartPanel     cartPanel;
    private OrderHistoryPanel historyPanel;
    private JLabel        cartTabLabel;

    public CustomerDashboard(User currentUser, Runnable onLogout) {
        this.currentUser = currentUser;
        this.onLogout    = onLogout;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);
        buildUI();
    }

    private void buildUI() {
        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel welcome = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcome.setFont(UITheme.FONT_BOLD);
        welcome.setForeground(Color.WHITE);

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            CartService.getInstance().clear();
            AuthService.getInstance().logout();
            onLogout.run();
        });

        topBar.add(welcome, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);

        // Restaurants tab with navigation to menu
        RestaurantListPanel restaurantPanel = new RestaurantListPanel(this::openRestaurant);
        tabs.addTab("🏪 Restaurants", restaurantPanel);

        // Cart tab
        cartPanel    = new CartPanel(this::onOrderPlaced);
        cartTabLabel = new JLabel("🛒 Cart (0)");
        tabs.addTab("🛒 Cart", cartPanel);

        // Orders tab
        historyPanel = new OrderHistoryPanel(currentUser.getId());
        tabs.addTab("📋 My Orders", historyPanel);

        add(tabs, BorderLayout.CENTER);
    }

    private void openRestaurant(Restaurant restaurant) {
        // Replace the Restaurants tab content with the menu panel temporarily
        MenuPanel menuPanel = new MenuPanel(
            restaurant,
            v -> updateCartBadge(),          // onCartChanged
            ()  -> {                          // onBack
                tabs.setComponentAt(0,
                    new RestaurantListPanel(this::openRestaurant));
                tabs.setSelectedIndex(0);
            }
        );
        tabs.setComponentAt(0, menuPanel);
        tabs.setSelectedIndex(0);
    }

    private void onOrderPlaced(Order order) {
        CartService.getInstance().clear();
        updateCartBadge();
        tabs.setSelectedIndex(2); // Switch to orders tab
        historyPanel.refresh();
        JOptionPane.showMessageDialog(this,
            "🎉 Order placed successfully!\nOrder ID: " + order.getId()
            + "\nTotal: " + String.format("%.2f BDT", order.getTotalAmount()),
            "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateCartBadge() {
        int count = CartService.getInstance().getTotalItems();
        tabs.setTitleAt(1, "🛒 Cart (" + count + ")");
        cartPanel.refresh();
    }
}
