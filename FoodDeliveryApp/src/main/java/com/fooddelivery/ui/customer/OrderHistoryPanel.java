package com.fooddelivery.ui.customer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.PaymentService;
import com.fooddelivery.service.RiderService;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Shows a customer's order history with live status tracking per order.
 */
public class OrderHistoryPanel extends JPanel {

    private final String  customerId;
    private JPanel        listPanel;

    public OrderHistoryPanel(String customerId) {
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
        List<Order> orders = OrderService.getInstance().getOrderHistory(customerId);

        if (orders.isEmpty()) {
            JLabel empty = new JLabel("No orders yet. Start ordering!", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_HEADING);
            empty.setForeground(UITheme.TEXT_MUTED);
            listPanel.add(empty);
        } else {
            for (Order order : orders) {
                listPanel.add(buildOrderCard(order));
                listPanel.add(Box.createVerticalStrut(8));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildOrderCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Top row: restaurant + date
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel restaurant = new JLabel(order.getRestaurantName());
        restaurant.setFont(UITheme.FONT_HEADING);
        restaurant.setForeground(UITheme.SECONDARY);
        JLabel dateLabel = UITheme.mutedLabel(order.getCreatedAt().toLocalDate().toString());
        topRow.add(restaurant, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);

        // Items summary
        StringBuilder sb = new StringBuilder();
        order.getItems().forEach(i -> sb.append(i.getQuantity()).append("× ")
                                        .append(i.getMenuItemName()).append("  "));
        JLabel itemsLabel = UITheme.mutedLabel(sb.toString().trim());

        // Status badge
        Color statusColor = switch (order.getStatus()) {
            case DELIVERED  -> UITheme.SUCCESS;
            case CANCELLED  -> UITheme.DANGER;
            case PLACED, CONFIRMED -> UITheme.STAR_COLOR;
            default -> UITheme.PRIMARY;
        };
        JLabel statusLabel = new JLabel("  " + order.getStatus().name() + "  ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(statusColor);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        // Totals
        JLabel totalLabel = new JLabel(String.format("%.2f BDT", order.getTotalAmount()));
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.PRIMARY);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        JButton trackBtn = UITheme.primaryButton("Track");
        trackBtn.addActionListener(e -> showTrackingDialog(order));
        btnRow.add(trackBtn);

        if (order.isCancellable()) {
            JButton cancelBtn = UITheme.dangerButton("Cancel");
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel this order?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    OrderService.getInstance().cancelOrder(order.getId());
                    refresh();
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

    private void showTrackingDialog(Order order) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     "Tracking: " + order.getId(), true);
        dialog.setSize(460, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        panel.setBackground(UITheme.CARD_BG);

        // Status steps
        OrderStatus[] steps = OrderStatus.values();
        int currentIdx = order.getStatus().ordinal();

        for (int i = 0; i < steps.length - 1; i++) { // exclude CANCELLED
            if (steps[i] == OrderStatus.CANCELLED) continue;
            JPanel stepRow = new JPanel(new BorderLayout(10, 0));
            stepRow.setOpaque(false);
            stepRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            boolean done   = i <= currentIdx;
            boolean active = i == currentIdx;

            JLabel dot = new JLabel(done ? "✅" : "○");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            dot.setPreferredSize(new Dimension(30, 30));

            JLabel stepLabel = new JLabel(steps[i].getDescription());
            stepLabel.setFont(active ? UITheme.FONT_BOLD : UITheme.FONT_BODY);
            stepLabel.setForeground(done ? UITheme.SUCCESS : UITheme.TEXT_MUTED);

            if (order.getStatusHistory().containsKey(steps[i])) {
                JLabel timeLabel = UITheme.mutedLabel(
                    order.getStatusHistory().get(steps[i]).toLocalTime().toString().substring(0, 8));
                stepRow.add(timeLabel, BorderLayout.EAST);
            }

            stepRow.add(dot, BorderLayout.WEST);
            stepRow.add(stepLabel, BorderLayout.CENTER);
            panel.add(stepRow);
            if (i < steps.length - 2) {
                JLabel line = new JLabel("   │");
                line.setForeground(UITheme.BORDER_COLOR);
                panel.add(line);
            }
        }

        // Rider info
        if (order.getRiderId() != null) {
            panel.add(UITheme.separator());
            RiderService.getInstance().findById(order.getRiderId()).ifPresent(r -> {
                JLabel riderLabel = new JLabel("🛵  Rider: " + r.getName() + "  ·  " + r.getPhone());
                riderLabel.setFont(UITheme.FONT_BODY);
                riderLabel.setForeground(UITheme.SECONDARY);
                panel.add(riderLabel);
            });
        }

        // Payment info
        PaymentService.getInstance().getPaymentForOrder(order.getId()).ifPresent(p -> {
            JLabel payLabel = UITheme.mutedLabel(
                "Payment: " + p.getMethod().name() + "  [" + p.getStatus().name() + "]");
            panel.add(payLabel);
        });

        // Advance status button (for demo / restaurant simulation)
        panel.add(Box.createVerticalStrut(12));
        if (order.getStatus() != OrderStatus.DELIVERED
         && order.getStatus() != OrderStatus.CANCELLED) {
            JButton advBtn = UITheme.primaryButton("▶ Simulate Next Status (Demo)");
            advBtn.addActionListener(e -> {
                OrderStatus next = OrderStatus.values()[order.getStatus().ordinal() + 1];
                if (next == OrderStatus.DELIVERED) {
                    OrderService.getInstance().completeDelivery(order.getId());
                } else {
                    OrderService.getInstance().advanceStatus(order.getId(), next);
                }
                dialog.dispose();
                refresh();
                JOptionPane.showMessageDialog(this,
                    "Status updated to: " + next, "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
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
