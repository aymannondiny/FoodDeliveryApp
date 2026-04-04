package com.fooddelivery.ui.restaurant;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.service.*;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Rider-facing dashboard.
 * Shows current assignment, available orders to pick up, and delivery history.
 */
public class RiderDashboard extends JPanel {

    private final User    riderUser;
    private final Runnable onLogout;
    private Rider         rider;
    private JPanel        contentPanel;

    public RiderDashboard(User riderUser, Runnable onLogout) {
        this.riderUser = riderUser;
        this.onLogout  = onLogout;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // Find or create rider profile
        List<Rider> all = RepositoryFactory.riders().findAll();
        this.rider = all.stream()
                        .filter(r -> riderUser.getId().equals(r.getUserId()))
                        .findFirst()
                        .orElse(null);
        if (rider == null) {
            rider = RiderService.getInstance().register(
                riderUser.getId(), riderUser.getName(),
                riderUser.getPhone(), "Bike");
        }
        buildUI();
    }

    private void buildUI() {
        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.SECONDARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("🛵  Rider Dashboard  –  " + riderUser.getName());
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
            RiderService.getInstance().setAvailability(rider.getId(), availToggle.isSelected());
            rider.setAvailable(availToggle.isSelected());
            availToggle.setText(rider.isAvailable() ? "● Available" : "● Unavailable");
            availToggle.setBackground(rider.isAvailable() ? UITheme.SUCCESS : UITheme.DANGER);
            refreshContent();
        });

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> { AuthService.getInstance().logout(); onLogout.run(); });

        right.add(availToggle);
        right.add(logoutBtn);
        topBar.add(title, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("📦 Current Assignment", buildCurrentTab());
        tabs.addTab("📋 All Deliveries",     buildHistoryTab());
        tabs.addTab("📊 Stats",              buildStatsTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Current Assignment ────────────────────────────────────────────────────

    private JPanel buildCurrentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hdr.setOpaque(false);
        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> {
            panel.removeAll();
            panel.add(hdr, BorderLayout.NORTH);
            panel.add(buildCurrentContent(), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
        });
        hdr.add(refresh);
        panel.add(hdr, BorderLayout.NORTH);
        panel.add(buildCurrentContent(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCurrentContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG);

        // Reload rider from store
        rider = RepositoryFactory.riders().findById(rider.getId()).orElse(rider);

        if (rider.getCurrentOrderId() == null) {
            // Show available (READY) orders to pick up
            List<Order> readyOrders = RepositoryFactory.orders()
                .findWhere(o -> o.getStatus() == OrderStatus.READY
                             && o.getRiderId() == null);

            JLabel heading = new JLabel(readyOrders.isEmpty()
                ? "No orders awaiting pickup." : "Orders Ready for Pickup:");
            heading.setFont(UITheme.FONT_HEADING);
            heading.setForeground(UITheme.SECONDARY);
            heading.setAlignmentX(LEFT_ALIGNMENT);
            panel.add(heading);
            panel.add(Box.createVerticalStrut(10));

            for (Order o : readyOrders) {
                panel.add(buildPickupCard(o));
                panel.add(Box.createVerticalStrut(6));
            }
        } else {
            // Show current order details with action buttons
            OrderService.getInstance().findById(rider.getCurrentOrderId()).ifPresent(o -> {
                panel.add(buildActiveDeliveryCard(o));
            });
        }
        return panel;
    }

    private JPanel buildPickupCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 3));
        info.setOpaque(false);
        info.add(new JLabel("Order #" + order.getId()));
        info.add(UITheme.mutedLabel("Restaurant: " + order.getRestaurantName()));
        info.add(UITheme.mutedLabel("Delivery: " + order.getDeliveryAddress()));
        card.add(info, BorderLayout.CENTER);

        JButton acceptBtn = UITheme.primaryButton("Accept Pickup");
        acceptBtn.addActionListener(e -> {
            // Assign rider manually
            rider.setCurrentOrderId(order.getId());
            rider.setAvailable(false);
            RepositoryFactory.riders().save(rider.getId(), rider);
            order.setRiderId(rider.getId());
            OrderService.getInstance().advanceStatus(order.getId(), OrderStatus.PICKED_UP);
            refreshContent();
        });
        card.add(acceptBtn, BorderLayout.EAST);
        return card;
    }

    private JPanel buildActiveDeliveryCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(14, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, UITheme.PRIMARY),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 18, 16, 18))));

        JLabel badge = new JLabel("ACTIVE DELIVERY", SwingConstants.CENTER);
        badge.setFont(UITheme.FONT_BOLD);
        badge.setForeground(Color.WHITE);
        badge.setBackground(UITheme.PRIMARY);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JPanel details = new JPanel(new GridLayout(5, 1, 0, 4));
        details.setOpaque(false);
        details.add(new JLabel("Order: " + order.getId()));
        details.add(new JLabel("Restaurant: " + order.getRestaurantName()));
        details.add(new JLabel("Deliver to: " + order.getDeliveryAddress()));
        details.add(new JLabel("Amount: " + String.format("%.2f BDT", order.getTotalAmount())));
        details.add(new JLabel("Payment: " + order.getPaymentMethod().name()));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        if (order.getStatus() == OrderStatus.PICKED_UP) {
            JButton onWayBtn = UITheme.primaryButton("▶ On the Way");
            onWayBtn.addActionListener(e -> {
                OrderService.getInstance().advanceStatus(order.getId(), OrderStatus.ON_THE_WAY);
                refreshContent();
            });
            buttons.add(onWayBtn);
        }

        if (order.getStatus() == OrderStatus.ON_THE_WAY) {
            JButton deliveredBtn = UITheme.primaryButton("✓ Mark Delivered");
            deliveredBtn.setBackground(UITheme.SUCCESS);
            deliveredBtn.addActionListener(e -> {
                OrderService.getInstance().completeDelivery(order.getId());
                JOptionPane.showMessageDialog(this,
                    "Delivery completed! 🎉", "Delivered", JOptionPane.INFORMATION_MESSAGE);
                refreshContent();
            });
            buttons.add(deliveredBtn);
        }

        card.add(badge,   BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    // ── History ───────────────────────────────────────────────────────────────

    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Order> delivered = RepositoryFactory.orders()
            .findWhere(o -> rider.getId().equals(o.getRiderId())
                        && o.getStatus() == OrderStatus.DELIVERED);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UITheme.BG);

        if (delivered.isEmpty()) {
            JLabel lbl = new JLabel("No deliveries yet.", SwingConstants.CENTER);
            lbl.setFont(UITheme.FONT_HEADING);
            lbl.setForeground(UITheme.TEXT_MUTED);
            list.add(lbl);
        } else {
            for (Order o : delivered) {
                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.setBackground(UITheme.CARD_BG);
                row.setBorder(UITheme.cardBorder());
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                row.add(new JLabel("Order #" + o.getId() + " – " + o.getRestaurantName()), BorderLayout.WEST);
                JLabel amt = new JLabel(String.format("%.2f BDT", o.getTotalAmount()));
                amt.setForeground(UITheme.PRIMARY);
                amt.setFont(UITheme.FONT_BOLD);
                row.add(amt, BorderLayout.EAST);

                list.add(row);
                list.add(Box.createVerticalStrut(5));
            }
        }

        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    private JPanel buildStatsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);

        long totalDeliveries = RepositoryFactory.orders()
            .findWhere(o -> rider.getId().equals(o.getRiderId())
                        && o.getStatus() == OrderStatus.DELIVERED).size();

        double totalEarnings = RepositoryFactory.orders()
            .findWhere(o -> rider.getId().equals(o.getRiderId())
                        && o.getStatus() == OrderStatus.DELIVERED)
            .stream().mapToDouble(Order::getDeliveryFee).sum();

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(12, 20, 12, 20);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        addStatRow(panel, gc, 0, "Total Deliveries",  String.valueOf(totalDeliveries));
        addStatRow(panel, gc, 1, "Total Earnings",    String.format("%.2f BDT", totalEarnings));
        addStatRow(panel, gc, 2, "Vehicle Type",      rider.getVehicleType());
        addStatRow(panel, gc, 3, "Status",            rider.isAvailable() ? "Available" : "Busy");

        return panel;
    }

    private void addStatRow(JPanel p, GridBagConstraints gc, int y, String label, String value) {
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
        p.add(card, gc);
    }

    private void refreshContent() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }
}
