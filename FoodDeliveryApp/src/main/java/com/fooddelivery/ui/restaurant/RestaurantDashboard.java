package com.fooddelivery.ui.restaurant;

// Explicit model imports — avoids ambiguity with java.awt.MenuItem
import com.fooddelivery.model.Address;
import com.fooddelivery.model.MenuItem;      // wins over java.awt.MenuItem (single-type beats wildcard)
import com.fooddelivery.model.MenuItemAddon;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Schedule;
import com.fooddelivery.model.User;
import com.fooddelivery.service.AuthService;
import com.fooddelivery.service.MenuService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;          // java.awt.MenuItem is shadowed by the explicit import above
import java.util.List;

/**
 * Dashboard for restaurant owners.
 * Features: menu management, live orders, schedule, and restaurant settings.
 */
public class RestaurantDashboard extends JPanel {

    private final User    owner;
    private final Runnable onLogout;
    private Restaurant    restaurant; // Currently selected/managed restaurant
    private JTabbedPane   tabs;
    private JPanel        ordersPanel;
    private JTable        menuTable;
    private DefaultTableModel menuModel;

    public RestaurantDashboard(User owner, Runnable onLogout) {
        this.owner    = owner;
        this.onLogout = onLogout;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // Find or prompt for restaurant
        List<Restaurant> owned = RestaurantService.getInstance().getByOwner(owner.getId());
        if (owned.isEmpty()) {
            showRegisterRestaurantDialog();
        } else {
            this.restaurant = owned.get(0); // Manage first restaurant
            buildUI();
        }
    }

    private void buildUI() {
        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.SECONDARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("🍽  " + restaurant.getName() + "  –  Owner Dashboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        JToggleButton openToggle = new JToggleButton(restaurant.isOpen() ? "● OPEN" : "● CLOSED");
        openToggle.setSelected(restaurant.isOpen());
        openToggle.setBackground(restaurant.isOpen() ? UITheme.SUCCESS : UITheme.DANGER);
        openToggle.setForeground(Color.WHITE);
        openToggle.setFont(UITheme.FONT_BOLD);
        openToggle.setBorderPainted(false);
        openToggle.addActionListener(e -> {
            restaurant.setOpen(openToggle.isSelected());
            RestaurantService.getInstance().updateRestaurant(restaurant);
            openToggle.setText(openToggle.isSelected() ? "● OPEN" : "● CLOSED");
            openToggle.setBackground(openToggle.isSelected() ? UITheme.SUCCESS : UITheme.DANGER);
        });

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> { AuthService.getInstance().logout(); onLogout.run(); });

        controls.add(openToggle);
        controls.add(logoutBtn);
        topBar.add(title, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("📋 Live Orders", buildOrdersTab());
        tabs.addTab("🍴 Menu",         buildMenuTab());
        tabs.addTab("⚙  Settings",     buildSettingsTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Live Orders ──────────────────────────────────────────────────────────

    private JPanel buildOrdersTab() {
        ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(UITheme.BG);

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        hdr.setBackground(UITheme.BG);
        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> refreshOrders());
        hdr.add(refresh, BorderLayout.EAST);
        ordersPanel.add(hdr, BorderLayout.NORTH);

        refreshOrders();
        return ordersPanel;
    }

    private void refreshOrders() {
        if (ordersPanel.getComponentCount() > 1)
            ordersPanel.remove(ordersPanel.getComponent(1));

        List<Order> active = OrderService.getInstance().getActiveRestaurantOrders(restaurant.getId());
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UITheme.BG);
        list.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        if (active.isEmpty()) {
            JLabel lbl = new JLabel("No active orders.", SwingConstants.CENTER);
            lbl.setFont(UITheme.FONT_HEADING);
            lbl.setForeground(UITheme.TEXT_MUTED);
            list.add(lbl);
        } else {
            for (Order o : active) list.add(buildOrderCard(o));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        ordersPanel.add(scroll, BorderLayout.CENTER);
        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private JPanel buildOrderCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel idLabel = new JLabel("Order #" + order.getId());
        idLabel.setFont(UITheme.FONT_BOLD);

        StringBuilder items = new StringBuilder();
        order.getItems().forEach(i -> items.append(i.getQuantity()).append("× ")
                                            .append(i.getMenuItemName()).append("  "));

        JLabel itemsLabel = UITheme.mutedLabel(items.toString());
        JLabel totalLabel = new JLabel(String.format("Total: %.2f BDT", order.getTotalAmount()));
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.PRIMARY);

        JLabel statusLabel = new JLabel(order.getStatus().getDescription());
        statusLabel.setForeground(UITheme.SUCCESS);

        JPanel info = new JPanel(new GridLayout(4, 1, 0, 2));
        info.setOpaque(false);
        info.add(idLabel);
        info.add(itemsLabel);
        info.add(totalLabel);
        info.add(statusLabel);
        card.add(info, BorderLayout.CENTER);

        // Advance status
        JPanel btns = new JPanel(new GridLayout(2, 1, 0, 4));
        btns.setOpaque(false);
        btns.setPreferredSize(new Dimension(160, 60));

        OrderStatus next = getNextStatus(order.getStatus());
        if (next != null) {
            JButton advBtn = UITheme.primaryButton("→ " + next.name());
            advBtn.addActionListener(e -> {
                if (next == OrderStatus.DELIVERED)
                    OrderService.getInstance().completeDelivery(order.getId());
                else
                    OrderService.getInstance().advanceStatus(order.getId(), next);
                refreshOrders();
            });
            btns.add(advBtn);
        }

        if (order.isCancellable()) {
            JButton cancelBtn = UITheme.dangerButton("✕ Cancel Order");
            cancelBtn.addActionListener(e -> {
                OrderService.getInstance().cancelOrder(order.getId());
                refreshOrders();
            });
            btns.add(cancelBtn);
        }
        card.add(btns, BorderLayout.EAST);
        card.add(Box.createVerticalStrut(6), BorderLayout.SOUTH);
        return card;
    }

    private OrderStatus getNextStatus(OrderStatus current) {
        return switch (current) {
            case PLACED    -> OrderStatus.CONFIRMED;
            case CONFIRMED -> OrderStatus.PREPARING;
            case PREPARING -> OrderStatus.READY;
            case READY     -> OrderStatus.PICKED_UP;
            case PICKED_UP -> OrderStatus.ON_THE_WAY;
            case ON_THE_WAY-> OrderStatus.DELIVERED;
            default        -> null;
        };
    }

    // ── Menu Management ──────────────────────────────────────────────────────

    private JPanel buildMenuTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        JButton addBtn    = UITheme.primaryButton("+ Add Item");
        JButton addonBtn  = UITheme.secondaryButton("+ Add-on");
        JButton toggleBtn = UITheme.secondaryButton("Toggle Available");
        JButton deleteBtn = UITheme.dangerButton("Delete");
        toolbar.add(addBtn); toolbar.add(addonBtn);
        toolbar.add(toggleBtn); toolbar.add(deleteBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Category", "Price (BDT)", "Available", "Stock"};
        menuModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = new JTable(menuModel);
        menuTable.setFont(UITheme.FONT_BODY);
        menuTable.setRowHeight(26);
        menuTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(menuTable);
        panel.add(scroll, BorderLayout.CENTER);

        loadMenuTable();

        // Actions
        addBtn.addActionListener(e -> showAddItemDialog());
        addonBtn.addActionListener(e -> showAddAddonDialog());
        toggleBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a menu item first."); return; }
            String itemId = (String) menuModel.getValueAt(row, 0);
            boolean current = (boolean) menuModel.getValueAt(row, 4);
            MenuService.getInstance().setAvailability(itemId, !current);
            loadMenuTable();
        });
        deleteBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a menu item first."); return; }
            String itemId = (String) menuModel.getValueAt(row, 0);
            MenuService.getInstance().deleteItem(itemId);
            loadMenuTable();
        });

        return panel;
    }

    private void loadMenuTable() {
        menuModel.setRowCount(0);
        MenuService.getInstance().getMenuForRestaurant(restaurant.getId()).forEach(item ->
            menuModel.addRow(new Object[]{
                item.getId(), item.getName(), item.getCategory(),
                String.format("%.2f", item.getPrice()),
                item.isAvailable(),
                item.getQuantity() == -1 ? "∞" : String.valueOf(item.getQuantity())
            })
        );
    }

    private void showAddItemDialog() {
        JTextField name  = UITheme.textField(20);
        JTextField cat   = UITheme.textField(20);
        JTextField price = UITheme.textField(10);
        JTextField desc  = UITheme.textField(30);
        JTextField qty   = UITheme.textField(10);
        qty.setText("-1");
        qty.setToolTipText("-1 = unlimited");

        Object[] fields = {"Name:", name, "Category:", cat, "Price (BDT):", price,
                           "Description:", desc, "Stock (-1 = unlimited):", qty};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Menu Item",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            MenuItem item = MenuService.getInstance().addMenuItem(
                restaurant.getId(), name.getText(), desc.getText(),
                cat.getText(), Double.parseDouble(price.getText())
            );
            int stock = Integer.parseInt(qty.getText());
            if (stock != -1) MenuService.getInstance().updateQuantity(item.getId(), stock);
            loadMenuTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Add Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAddonDialog() {
        int row = menuTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a menu item first."); return; }
        String itemId = (String) menuModel.getValueAt(row, 0);

        JTextField name  = UITheme.textField(20);
        JTextField price = UITheme.textField(10);
        price.setText("0");

        Object[] fields = {"Add-on Name:", name, "Extra Price (BDT):", price};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Add-on", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            MenuService.getInstance().addAddon(itemId, name.getText(), Double.parseDouble(price.getText()));
            JOptionPane.showMessageDialog(this, "Add-on added successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── Settings ─────────────────────────────────────────────────────────────

    private JPanel buildSettingsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JTextField nameField  = UITheme.textField(24); nameField.setText(restaurant.getName());
        JTextField phoneField = UITheme.textField(24); phoneField.setText(restaurant.getPhoneNumber());
        JTextField minOrdFld  = UITheme.textField(10); minOrdFld.setText(String.valueOf((int)restaurant.getMinOrderAmount()));
        JTextField etaFld     = UITheme.textField(10); etaFld.setText(String.valueOf(restaurant.getEstimatedDeliveryMinutes()));
        JTextField openTime   = UITheme.textField(10); openTime.setText("09:00");
        JTextField closeTime  = UITheme.textField(10); closeTime.setText("23:00");
        JTextField descField  = UITheme.textField(30); descField.setText(restaurant.getDescription() != null ? restaurant.getDescription() : "");

        String[][] rows = {{"Restaurant Name:", null}, {"Phone:", null},
                           {"Min Order (BDT):", null}, {"Estimated Delivery (min):", null},
                           {"Open Time (HH:mm):", null}, {"Close Time (HH:mm):", null},
                           {"Description:", null}};
        Component[] fields = {nameField, phoneField, minOrdFld, etaFld, openTime, closeTime, descField};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i * 2;     p(panel, new JLabel(rows[i][0]), gc);
            gc.gridy = i * 2 + 1; p(panel, fields[i], gc);
        }

        JButton save = UITheme.primaryButton("Save Settings");
        save.addActionListener(e -> {
            restaurant.setName(nameField.getText());
            restaurant.setPhoneNumber(phoneField.getText());
            restaurant.setDescription(descField.getText());
            try {
                restaurant.setMinOrderAmount(Double.parseDouble(minOrdFld.getText()));
                restaurant.setEstimatedDeliveryMinutes(Integer.parseInt(etaFld.getText()));
                Schedule schedule = Schedule.allDay(openTime.getText(), closeTime.getText());
                restaurant.setSchedule(schedule);
            } catch (Exception ignored) {}
            RestaurantService.getInstance().updateRestaurant(restaurant);
            JOptionPane.showMessageDialog(this, "Settings saved!");
        });

        gc.gridy = rows.length * 2 + 1;
        p(panel, save, gc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        return wrapper;
    }

    private void p(JPanel panel, Component c, GridBagConstraints gc) {
        panel.add(c, gc);
    }

    private void showRegisterRestaurantDialog() {
        JTextField name    = UITheme.textField(22);
        JTextField cuisine = UITheme.textField(22);
        JTextField street  = UITheme.textField(22);
        JTextField area    = UITheme.textField(22);
        JTextField phone   = UITheme.textField(22);
        JTextField minOrd  = UITheme.textField(10); minOrd.setText("150");
        JTextField eta     = UITheme.textField(10); eta.setText("30");

        Object[] fields = {
            "Restaurant Name:", name, "Cuisine Type:", cuisine,
            "Street Address:", street, "Area:", area,
            "Phone:", phone, "Min Order (BDT):", minOrd, "Est. Delivery (min):", eta
        };
        int ok = JOptionPane.showConfirmDialog(this, fields, "Register Your Restaurant",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) { onLogout.run(); return; }

        try {
            Address addr = new Address(street.getText(), area.getText(), "Dhaka", "");
            restaurant = RestaurantService.getInstance().register(
                owner.getId(), name.getText(), cuisine.getText(), addr,
                phone.getText(), 15.0,
                Double.parseDouble(minOrd.getText()),
                Integer.parseInt(eta.getText())
            );
            buildUI();
            revalidate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            onLogout.run();
        }
    }
}
