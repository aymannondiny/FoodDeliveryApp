package com.fooddelivery.ui.customer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.service.AuthService;
import com.fooddelivery.service.CartService;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.customer.cart.CartController;
import com.fooddelivery.ui.customer.menu.MenuController;
import com.fooddelivery.ui.customer.orders.OrderHistoryController;
import com.fooddelivery.ui.customer.restaurants.RestaurantListController;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;

import static com.fooddelivery.service.AuthService.*;

/**
 * Top-level container for the customer experience.
 * Manages navigation between: Restaurants → Menu → Cart → Orders.
 */
public class CustomerDashboard extends JPanel {

    private final User currentUser;
    private final Runnable onLogout;
    private final RestaurantListController restaurantListController;
    private final MenuController menuController;
    private final CartController cartController;
    private final OrderHistoryController orderHistoryController;

    private JTabbedPane tabs;
    private CartPanel cartPanel;
    private OrderHistoryPanel historyPanel;

    public CustomerDashboard(User currentUser,
                             Runnable onLogout,
                             RestaurantListController restaurantListController,
                             MenuController menuController,
                             CartController cartController,
                             OrderHistoryController orderHistoryController) {
        this.currentUser = currentUser;
        this.onLogout = onLogout;
        this.restaurantListController = restaurantListController;
        this.menuController = menuController;
        this.cartController = cartController;
        this.orderHistoryController = orderHistoryController;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);
        buildUI();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel welcome = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcome.setFont(UITheme.FONT_BOLD);
        welcome.setForeground(Color.WHITE);

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            cartController.clearCart();
            AuthService.getInstance().logout();
            onLogout.run();
        });

        topBar.add(welcome, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);

        tabs.addTab("🏪 Restaurants", createRestaurantListPanel());

        cartPanel = new CartPanel(cartController, this::onOrderPlaced);
        tabs.addTab("🛒 Cart", cartPanel);

        historyPanel = new OrderHistoryPanel(orderHistoryController, currentUser.getId());
        tabs.addTab("📋 My Orders", historyPanel);

        add(tabs, BorderLayout.CENTER);
    }

    private RestaurantListPanel createRestaurantListPanel() {
        return new RestaurantListPanel(restaurantListController, this::openRestaurant);
    }

    private void openRestaurant(Restaurant restaurant) {
        MenuPanel menuPanel = new MenuPanel(
                restaurant,
                menuController,
                v -> updateCartBadge(),
                () -> {
                    tabs.setComponentAt(0, createRestaurantListPanel());
                    tabs.setSelectedIndex(0);
                }
        );

        tabs.setComponentAt(0, menuPanel);
        tabs.setSelectedIndex(0);
    }

    private void onOrderPlaced(Order order) {
        updateCartBadge();
        tabs.setSelectedIndex(2);
        historyPanel.refresh();

        JOptionPane.showMessageDialog(
                this,
                "🎉 Order placed successfully!\nOrder ID: " + order.getId()
                        + "\nTotal: " + String.format("%.2f BDT", order.getTotalAmount()),
                "Order Confirmed",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateCartBadge() {
        int count = cartController.loadCartView().getTotalItems();
        tabs.setTitleAt(1, "🛒 Cart (" + count + ")");
        cartPanel.refresh();
    }
}