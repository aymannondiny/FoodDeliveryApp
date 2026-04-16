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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel restaurant = new JLabel(order.getRestaurantName());
        restaurant.setFont(UITheme.FONT_HEADING);
        restaurant.setForeground(UITheme.SECONDARY);

        JLabel dateLabel = UITheme.mutedLabel(order.getDateText());

        topRow.add(restaurant, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);

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

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(btnRow, BorderLayout.EAST);

        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(itemsLabel, BorderLayout.WEST);
        mid.add(statusLabel, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
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