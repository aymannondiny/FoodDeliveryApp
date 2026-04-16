package com.fooddelivery.ui.customer;

import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.customer.orders.OrderHistoryController;
import com.fooddelivery.ui.customer.orders.viewmodel.OrderSummaryViewModel;
import com.fooddelivery.ui.customer.orders.viewmodel.TrackingStepViewModel;
import com.fooddelivery.ui.customer.orders.viewmodel.TrackingViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Shows a customer's order history with live status tracking per order.
 * Delegates orchestration to OrderHistoryController.
 */
public class OrderHistoryPanel extends JPanel {

    private final String customerId;
    private final OrderHistoryController controller;

    private JPanel listPanel;

    public OrderHistoryPanel(OrderHistoryController controller, String customerId) {
        this.controller = controller;
        this.customerId = customerId;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SECONDARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("📋  My Orders");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JButton refreshBtn = UITheme.secondaryButton("↻ Refresh");
        refreshBtn.addActionListener(e -> refresh());

        header.add(title, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        listPanel.removeAll();
        List<OrderSummaryViewModel> orders = controller.loadOrderHistory(customerId);

        if (orders.isEmpty()) {
            JLabel empty = new JLabel("No orders yet. Start ordering!", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_HEADING);
            empty.setForeground(UITheme.TEXT_MUTED);
            listPanel.add(empty);
        } else {
            for (OrderSummaryViewModel order : orders) {
                listPanel.add(buildOrderCard(order));
                listPanel.add(Box.createVerticalStrut(8));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildOrderCard(OrderSummaryViewModel order) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel restaurant = new JLabel(order.getRestaurantName());
        restaurant.setFont(UITheme.FONT_HEADING);
        restaurant.setForeground(UITheme.SECONDARY);

        JLabel dateLabel = UITheme.mutedLabel(order.getDateText());

        topRow.add(restaurant, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);

        JLabel orderIdLabel = UITheme.mutedLabel("Order #" + order.getOrderId());
        orderIdLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel itemsLabel = UITheme.mutedLabel(order.getItemsSummaryText());

        Color statusColor = switch (order.getStatus()) {
            case DELIVERED -> UITheme.SUCCESS;
            case CANCELLED -> UITheme.DANGER;
            case PLACED, CONFIRMED -> UITheme.STAR_COLOR;
            default -> UITheme.PRIMARY;
        };

        JLabel statusLabel = new JLabel("  " + order.getStatusText() + "  ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(statusColor);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel totalLabel = new JLabel(order.getTotalText());
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.PRIMARY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);

        JButton trackBtn = UITheme.primaryButton("Track");
        trackBtn.addActionListener(e -> showTrackingDialog(order.getOrderId()));
        btnRow.add(trackBtn);

        if (order.isCancellable()) {
            JButton cancelBtn = UITheme.dangerButton("Cancel");
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Cancel this order?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.cancelOrder(order.getOrderId());
                        refresh();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                this,
                                ex.getMessage(),
                                "Cancellation Failed",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });
            btnRow.add(cancelBtn);
        }

        if (order.isRateable()) {
            JButton rateBtn = UITheme.primaryButton("⭐ Rate");
            rateBtn.setBackground(UITheme.STAR_COLOR);
            rateBtn.addActionListener(e -> showRatingDialog(order));
            btnRow.add(rateBtn);
        }

        if (order.isRated()) {
            JLabel ratedLabel = new JLabel("  ✓ Rated  ");
            ratedLabel.setFont(UITheme.FONT_SMALL);
            ratedLabel.setForeground(UITheme.SUCCESS);
            btnRow.add(ratedLabel);
        }

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(btnRow, BorderLayout.EAST);

        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(itemsLabel, BorderLayout.WEST);
        mid.add(statusLabel, BorderLayout.EAST);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);
        topRow.setAlignmentX(LEFT_ALIGNMENT);
        orderIdLabel.setAlignmentX(LEFT_ALIGNMENT);
        northPanel.add(topRow);
        northPanel.add(orderIdLabel);

        card.add(northPanel, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private void showRatingDialog(OrderSummaryViewModel order) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Rate Order – " + order.getRestaurantName(),
                true
        );
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(UITheme.CARD_BG);

        JLabel orderLabel = new JLabel("Order #" + order.getOrderId());
        orderLabel.setFont(UITheme.FONT_BOLD);
        orderLabel.setForeground(UITheme.SECONDARY);
        orderLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel restaurantLabel = new JLabel("From: " + order.getRestaurantName());
        restaurantLabel.setFont(UITheme.FONT_BODY);
        restaurantLabel.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(orderLabel);
        panel.add(restaurantLabel);
        panel.add(Box.createVerticalStrut(20));

        JLabel foodLabel = new JLabel("🍽  Rate the food:");
        foodLabel.setFont(UITheme.FONT_BOLD);
        foodLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(foodLabel);
        panel.add(Box.createVerticalStrut(4));

        JPanel foodStars = createStarPanel();
        foodStars.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(foodStars);

        panel.add(Box.createVerticalStrut(16));

        JLabel riderLabel = new JLabel("🛵  Rate the rider:");
        riderLabel.setFont(UITheme.FONT_BOLD);
        riderLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(riderLabel);
        panel.add(Box.createVerticalStrut(4));

        JPanel riderStars = createStarPanel();
        riderStars.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(riderStars);

        dialog.add(panel, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton cancelBtn = UITheme.secondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = UITheme.primaryButton("Submit Rating");
        submitBtn.setBackground(UITheme.STAR_COLOR);
        submitBtn.addActionListener(e -> {
            int foodRating = getSelectedRating(foodStars);
            int riderRating = getSelectedRating(riderStars);

            if (foodRating == 0 || riderRating == 0) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a rating for both food and rider.",
                        "Rating Required",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                controller.rateOrder(order.getOrderId(), foodRating, riderRating);
                dialog.dispose();
                refresh();

                JOptionPane.showMessageDialog(
                        this,
                        String.format(
                                "Thank you for rating!\nFood: %s\nRider: %s",
                                "★".repeat(foodRating),
                                "★".repeat(riderRating)
                        ),
                        "Rating Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        ex.getMessage(),
                        "Rating Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(submitBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createStarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();

        for (int i = 1; i <= 5; i++) {
            JToggleButton star = new JToggleButton(String.valueOf(i) + " ★");
            star.setFont(UITheme.FONT_BOLD);
            star.setFocusPainted(false);
            star.setBackground(UITheme.CARD_BG);
            star.setForeground(UITheme.TEXT_MUTED);
            star.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            star.putClientProperty("rating", i);

            final int rating = i;
            star.addActionListener(e -> {
                for (Component c : panel.getComponents()) {
                    if (c instanceof JToggleButton) {
                        JToggleButton btn = (JToggleButton) c;
                        int btnRating = (int) btn.getClientProperty("rating");
                        if (btnRating <= rating) {
                            btn.setBackground(UITheme.STAR_COLOR);
                            btn.setForeground(Color.WHITE);
                        } else {
                            btn.setBackground(UITheme.CARD_BG);
                            btn.setForeground(UITheme.TEXT_MUTED);
                        }
                    }
                }
            });

            group.add(star);
            panel.add(star);
        }

        return panel;
    }

    private int getSelectedRating(JPanel starPanel) {
        int maxSelected = 0;
        for (Component c : starPanel.getComponents()) {
            if (c instanceof JToggleButton) {
                JToggleButton btn = (JToggleButton) c;
                int rating = (int) btn.getClientProperty("rating");
                if (btn.getBackground().equals(UITheme.STAR_COLOR)) {
                    maxSelected = Math.max(maxSelected, rating);
                }
            }
        }
        return maxSelected;
    }

    private void showTrackingDialog(String orderId) {
        TrackingViewModel tracking;
        try {
            tracking = controller.loadTracking(orderId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Tracking Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tracking: " + tracking.getOrderId(),
                true
        );
        dialog.setSize(460, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        panel.setBackground(UITheme.CARD_BG);

        for (int i = 0; i < tracking.getSteps().size(); i++) {
            TrackingStepViewModel step = tracking.getSteps().get(i);

            JPanel stepRow = new JPanel(new BorderLayout(10, 0));
            stepRow.setOpaque(false);
            stepRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel dot = new JLabel(step.isDone() ? "✅" : "○");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            dot.setPreferredSize(new Dimension(30, 30));

            JLabel stepLabel = new JLabel(step.getLabel());
            stepLabel.setFont(step.isActive() ? UITheme.FONT_BOLD : UITheme.FONT_BODY);
            stepLabel.setForeground(step.isDone() ? UITheme.SUCCESS : UITheme.TEXT_MUTED);

            if (step.getTimeText() != null) {
                JLabel timeLabel = UITheme.mutedLabel(step.getTimeText());
                stepRow.add(timeLabel, BorderLayout.EAST);
            }

            stepRow.add(dot, BorderLayout.WEST);
            stepRow.add(stepLabel, BorderLayout.CENTER);
            panel.add(stepRow);

            if (i < tracking.getSteps().size() - 1) {
                JLabel line = new JLabel("   │");
                line.setForeground(UITheme.BORDER_COLOR);
                panel.add(line);
            }
        }

        if (tracking.getRiderText() != null) {
            panel.add(UITheme.separator());
            JLabel riderLabel = new JLabel(tracking.getRiderText());
            riderLabel.setFont(UITheme.FONT_BODY);
            riderLabel.setForeground(UITheme.SECONDARY);
            panel.add(riderLabel);
        }

        if (tracking.getPaymentText() != null) {
            JLabel payLabel = UITheme.mutedLabel(tracking.getPaymentText());
            panel.add(payLabel);
        }

        panel.add(Box.createVerticalStrut(12));

        if (tracking.canAdvanceDemo()) {
            JButton advBtn = UITheme.primaryButton("▶ Simulate Next Status (Demo)");
            advBtn.addActionListener(e -> {
                try {
                    String next = controller.advanceDemoStatus(orderId);
                    dialog.dispose();
                    refresh();

                    JOptionPane.showMessageDialog(
                            this,
                            "Status updated to: " + next,
                            "Status Updated",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            ex.getMessage(),
                            "Status Update Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            panel.add(advBtn);
        }

        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);

        JButton close = UITheme.secondaryButton("Close");
        close.addActionListener(e -> dialog.dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(close);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}