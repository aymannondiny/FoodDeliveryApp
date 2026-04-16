package com.fooddelivery.ui.restaurant;

import com.fooddelivery.model.Rider;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.restaurant.riderdashboard.RiderDashboardController;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderCurrentOrderViewModel;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderDeliveryHistoryRowViewModel;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderPickupOrderViewModel;
import com.fooddelivery.ui.restaurant.riderdashboard.viewmodel.RiderStatsViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Rider-facing dashboard.
 * UI-only responsibilities; workflow is delegated to RiderDashboardController.
 */
public class RiderDashboard extends JPanel {

    private final User riderUser;
    private final Runnable onLogout;
    private final RiderDashboardController controller;

    private Rider rider;
    private JTabbedPane tabs;
    private int lastSelectedTab = 0;

    public RiderDashboard(User riderUser,
                          Runnable onLogout,
                          RiderDashboardController controller) {
        this.riderUser = riderUser;
        this.onLogout = onLogout;
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        this.rider = controller.ensureRiderProfile(riderUser);
        buildUI();
    }

    private void buildUI() {
        removeAll();

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.SECONDARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        // Reload to get synced rating from controller's stats computation
        rider = controller.reloadRider(rider.getId());

        String riderRatingDisplay = rider.getTotalRatings() > 0
                ? String.format("  ★ %.1f (%d)", rider.getRating(), rider.getTotalRatings())
                : "";

        JLabel title = new JLabel("🛵  Rider Dashboard  –  " + riderUser.getName() + riderRatingDisplay);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JToggleButton availToggle = new JToggleButton(rider.isAvailable() ? "● Available" : "● Unavailable");
        availToggle.setSelected(rider.isAvailable());
        availToggle.setBackground(rider.isAvailable() ? UITheme.SUCCESS : UITheme.DANGER);
        availToggle.setForeground(Color.WHITE);
        availToggle.setFont(UITheme.FONT_BOLD);
        availToggle.setBorderPainted(false);
        availToggle.addActionListener(e -> {
            try {
                rider = controller.updateAvailability(rider.getId(), availToggle.isSelected());
                availToggle.setText(rider.isAvailable() ? "● Available" : "● Unavailable");
                availToggle.setBackground(rider.isAvailable() ? UITheme.SUCCESS : UITheme.DANGER);
                refreshContent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Availability Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                availToggle.setSelected(rider.isAvailable());
            }
        });

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            controller.logout();
            onLogout.run();
        });

        right.add(availToggle);
        right.add(logoutBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("📦 Current Assignment", buildCurrentTab());
        tabs.addTab("📋 All Deliveries", buildHistoryTab());
        tabs.addTab("📊 Stats", buildStatsTab());

        if (lastSelectedTab >= 0 && lastSelectedTab < tabs.getTabCount()) {
            tabs.setSelectedIndex(lastSelectedTab);
        }

        add(tabs, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel buildCurrentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hdr.setOpaque(false);

        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> refreshContent());

        hdr.add(refresh);
        panel.add(hdr, BorderLayout.NORTH);
        panel.add(buildCurrentContent(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCurrentContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG);

        rider = controller.reloadRider(rider.getId());

        if (rider.getCurrentOrderId() == null) {
            List<RiderPickupOrderViewModel> readyOrders = controller.loadReadyForPickupOrders();

            JLabel heading = new JLabel(
                    readyOrders.isEmpty()
                            ? "No orders awaiting pickup."
                            : "Orders Ready for Pickup:"
            );
            heading.setFont(UITheme.FONT_HEADING);
            heading.setForeground(UITheme.SECONDARY);
            heading.setAlignmentX(LEFT_ALIGNMENT);

            panel.add(heading);
            panel.add(Box.createVerticalStrut(10));

            for (RiderPickupOrderViewModel order : readyOrders) {
                panel.add(buildPickupCard(order));
                panel.add(Box.createVerticalStrut(6));
            }
        } else {
            RiderCurrentOrderViewModel current = controller.loadCurrentAssignment(rider.getId());
            if (current != null) {
                panel.add(buildActiveDeliveryCard(current));
            }
        }

        return panel;
    }

    private JPanel buildPickupCard(RiderPickupOrderViewModel order) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 3));
        info.setOpaque(false);
        info.add(new JLabel("Order #" + order.getOrderId()));
        info.add(UITheme.mutedLabel("Restaurant: " + order.getRestaurantName()));
        info.add(UITheme.mutedLabel("Delivery: " + order.getDeliveryAddressText()));
        card.add(info, BorderLayout.CENTER);

        JButton acceptBtn = UITheme.primaryButton("Accept Pickup");
        acceptBtn.addActionListener(e -> {
            try {
                controller.acceptPickup(rider.getId(), order.getOrderId());
                refreshContent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Pickup Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        card.add(acceptBtn, BorderLayout.EAST);
        return card;
    }

    private JPanel buildActiveDeliveryCard(RiderCurrentOrderViewModel order) {
        JPanel card = new JPanel(new BorderLayout(14, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, UITheme.PRIMARY),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                        BorderFactory.createEmptyBorder(16, 18, 16, 18)
                )
        ));

        JLabel badge = new JLabel("ACTIVE DELIVERY", SwingConstants.CENTER);
        badge.setFont(UITheme.FONT_BOLD);
        badge.setForeground(Color.WHITE);
        badge.setBackground(UITheme.PRIMARY);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JPanel details = new JPanel(new GridLayout(5, 1, 0, 4));
        details.setOpaque(false);
        details.add(new JLabel("Order: " + order.getOrderId()));
        details.add(new JLabel("Restaurant: " + order.getRestaurantName()));
        details.add(new JLabel("Deliver to: " + order.getDeliveryAddressText()));
        details.add(new JLabel("Amount: " + order.getAmountText()));
        details.add(new JLabel("Payment: " + order.getPaymentText()));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        if (order.isCanMarkOnTheWay()) {
            JButton onWayBtn = UITheme.primaryButton("▶ On the Way");
            onWayBtn.addActionListener(e -> {
                try {
                    controller.markOnTheWay(order.getOrderId());
                    refreshContent();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Status Update Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            buttons.add(onWayBtn);
        }

        if (order.isCanMarkDelivered()) {
            JButton deliveredBtn = UITheme.primaryButton("✓ Mark Delivered");
            deliveredBtn.setBackground(UITheme.SUCCESS);
            deliveredBtn.addActionListener(e -> {
                try {
                    controller.markDelivered(order.getOrderId());
                    JOptionPane.showMessageDialog(
                            this,
                            "Delivery completed! 🎉",
                            "Delivered",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshContent();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Delivery Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            buttons.add(deliveredBtn);
        }

        card.add(badge, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hdr.setOpaque(false);

        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> refreshHistoryTab(panel));
        hdr.add(refresh);

        panel.add(hdr, BorderLayout.NORTH);
        panel.add(buildHistoryContent(), BorderLayout.CENTER);

        return panel;
    }

    private void refreshHistoryTab(JPanel panel) {
        if (panel.getComponentCount() > 1) {
            panel.remove(panel.getComponent(1));
        }

        panel.add(buildHistoryContent(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JScrollPane buildHistoryContent() {
        List<RiderDeliveryHistoryRowViewModel> delivered = controller.loadDeliveryHistory(rider.getId());

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UITheme.BG);

        if (delivered.isEmpty()) {
            JLabel lbl = new JLabel("No deliveries yet.", SwingConstants.CENTER);
            lbl.setFont(UITheme.FONT_HEADING);
            lbl.setForeground(UITheme.TEXT_MUTED);
            list.add(lbl);
        } else {
            for (RiderDeliveryHistoryRowViewModel order : delivered) {
                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.setBackground(UITheme.CARD_BG);
                row.setBorder(UITheme.cardBorder());
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, order.isRated() ? 80 : 60));

                JPanel leftInfo = new JPanel(new GridLayout(order.isRated() ? 2 : 1, 1, 0, 2));
                leftInfo.setOpaque(false);

                leftInfo.add(new JLabel("Order #" + order.getOrderId() + " – " + order.getRestaurantName()));

                if (order.isRated() && order.getRiderRatingText() != null) {
                    JLabel ratingLabel = new JLabel("⭐ Customer rated you: " + order.getRiderRatingText());
                    ratingLabel.setFont(UITheme.FONT_SMALL);
                    ratingLabel.setForeground(UITheme.STAR_COLOR);
                    leftInfo.add(ratingLabel);
                }

                row.add(leftInfo, BorderLayout.CENTER);

                JLabel amt = new JLabel(order.getTotalAmountText());
                amt.setForeground(UITheme.PRIMARY);
                amt.setFont(UITheme.FONT_BOLD);
                row.add(amt, BorderLayout.EAST);

                list.add(row);
                list.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel buildStatsTab() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(UITheme.BG);

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hdr.setOpaque(false);

        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> refreshStatsTab(outerPanel));
        hdr.add(refresh);

        outerPanel.add(hdr, BorderLayout.NORTH);
        outerPanel.add(buildStatsContent(), BorderLayout.CENTER);

        return outerPanel;
    }

    private void refreshStatsTab(JPanel panel) {
        if (panel.getComponentCount() > 1) {
            panel.remove(panel.getComponent(1));
        }

        panel.add(buildStatsContent(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JPanel buildStatsContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);

        RiderStatsViewModel stats = controller.loadStats(rider.getId());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(12, 20, 12, 20);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        addStatRow(panel, gc, 0, "Total Deliveries", stats.getTotalDeliveriesText());
        addStatRow(panel, gc, 1, "Total Earnings", stats.getTotalEarningsText());
        addStatRow(panel, gc, 2, "Average Rating", stats.getAverageRatingText());
        addStatRow(panel, gc, 3, "Vehicle Type", stats.getVehicleTypeText());
        addStatRow(panel, gc, 4, "Status", stats.getStatusText());

        return panel;
    }

    private void addStatRow(JPanel panel,
                            GridBagConstraints gc,
                            int y,
                            String label,
                            String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_TITLE);
        val.setForeground(UITheme.PRIMARY);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);

        gc.gridy = y;
        panel.add(card, gc);
    }

    private void refreshContent() {
        rider = controller.reloadRider(rider.getId());

        if (tabs != null) {
            lastSelectedTab = tabs.getSelectedIndex();
        }

        removeAll();
        buildUI();
        revalidate();
        repaint();
    }
}