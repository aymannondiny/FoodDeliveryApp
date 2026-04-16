package com.fooddelivery.ui.restaurant;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.restaurant.dashboard.RestaurantDashboardController;
import com.fooddelivery.ui.restaurant.dashboard.request.MenuAddonForm;
import com.fooddelivery.ui.restaurant.dashboard.request.MenuItemForm;
import com.fooddelivery.ui.restaurant.dashboard.request.RestaurantRegistrationForm;
import com.fooddelivery.ui.restaurant.dashboard.request.RestaurantSettingsForm;
import com.fooddelivery.ui.restaurant.dashboard.viewmodel.RestaurantMenuRowViewModel;
import com.fooddelivery.ui.restaurant.dashboard.viewmodel.RestaurantOrderViewModel;
import com.fooddelivery.ui.restaurant.dashboard.viewmodel.RestaurantSettingsViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dashboard for restaurant owners.
 * UI-only responsibilities; workflow is delegated to RestaurantDashboardController.
 */
public class RestaurantDashboard extends JPanel {

    private final User owner;
    private final Runnable onLogout;
    private final RestaurantDashboardController controller;

    private Restaurant restaurant;
    private JTabbedPane tabs;
    private JPanel ordersPanel;
    private JTable menuTable;
    private DefaultTableModel menuModel;
    private JLabel titleLabel;

    public RestaurantDashboard(User owner,
                               Runnable onLogout,
                               RestaurantDashboardController controller) {
        this.owner = owner;
        this.onLogout = onLogout;
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        controller.findManagedRestaurant(owner.getId()).ifPresentOrElse(
                found -> {
                    this.restaurant = found;
                    buildUI();
                },
                this::showRegisterRestaurantDialog
        );
    }

    private void buildUI() {
        removeAll();

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.SECONDARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        String ratingDisplay = restaurant.getTotalRatings() > 0
                ? String.format("  ★ %.1f (%d)", restaurant.getRating(), restaurant.getTotalRatings())
                : "";

        titleLabel = new JLabel("🍽  " + restaurant.getName() + ratingDisplay + "  –  Owner Dashboard");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        JToggleButton openToggle = new JToggleButton(restaurant.isOpen() ? "● OPEN" : "● CLOSED");
        openToggle.setSelected(restaurant.isOpen());
        openToggle.setBackground(restaurant.isOpen() ? UITheme.SUCCESS : UITheme.DANGER);
        openToggle.setForeground(Color.WHITE);
        openToggle.setFont(UITheme.FONT_BOLD);
        openToggle.setBorderPainted(false);
        openToggle.addActionListener(e -> {
            try {
                restaurant = controller.updateOpenStatus(restaurant, openToggle.isSelected());
                openToggle.setText(openToggle.isSelected() ? "● OPEN" : "● CLOSED");
                openToggle.setBackground(openToggle.isSelected() ? UITheme.SUCCESS : UITheme.DANGER);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Status Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                openToggle.setSelected(restaurant.isOpen());
            }
        });

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            controller.logout();
            onLogout.run();
        });

        controls.add(openToggle);
        controls.add(logoutBtn);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("📋 Live Orders", buildOrdersTab());
        tabs.addTab("📦 All Orders", buildAllOrdersTab());
        tabs.addTab("🍴 Menu", buildMenuTab());
        tabs.addTab("⚙  Settings", buildSettingsTab());

        add(tabs, BorderLayout.CENTER);

        revalidate();
        repaint();
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
        if (ordersPanel.getComponentCount() > 1) {
            ordersPanel.remove(ordersPanel.getComponent(1));
        }

        List<RestaurantOrderViewModel> active = controller.loadActiveOrders(restaurant.getId());

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
            for (RestaurantOrderViewModel order : active) {
                list.add(buildOrderCard(order));
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        ordersPanel.add(scroll, BorderLayout.CENTER);
        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private JPanel buildOrderCard(RestaurantOrderViewModel order) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, order.isRated() ? 155 : 130));

        JLabel idLabel = new JLabel("Order #" + order.getOrderId());
        idLabel.setFont(UITheme.FONT_BOLD);

        JLabel itemsLabel = UITheme.mutedLabel(order.getItemsSummary());

        JLabel totalLabel = new JLabel(order.getTotalText());
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.PRIMARY);

        JLabel statusLabel = new JLabel(order.getStatusDescription());
        statusLabel.setForeground(UITheme.SUCCESS);

        int infoRows = order.isRated() ? 5 : 4;
        JPanel info = new JPanel(new GridLayout(infoRows, 1, 0, 2));
        info.setOpaque(false);
        info.add(idLabel);
        info.add(itemsLabel);
        info.add(totalLabel);
        info.add(statusLabel);

        if (order.isRated()) {
            JLabel ratingLabel = new JLabel("⭐ Customer rated: " + order.getFoodRatingText());
            ratingLabel.setFont(UITheme.FONT_SMALL);
            ratingLabel.setForeground(UITheme.STAR_COLOR);
            info.add(ratingLabel);
        }

        card.add(info, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(2, 1, 0, 4));
        btns.setOpaque(false);
        btns.setPreferredSize(new Dimension(160, 60));

        if (order.getNextStatus() != null) {
            JButton advBtn = UITheme.primaryButton(order.getNextActionText());
            advBtn.addActionListener(e -> {
                try {
                    controller.advanceOrderToNextStatus(order.getOrderId(), order.getCurrentStatus());
                    refreshOrders();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Status Update Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            btns.add(advBtn);
        }

        if (order.isCancellable()) {
            JButton cancelBtn = UITheme.dangerButton("✕ Cancel Order");
            cancelBtn.addActionListener(e -> {
                try {
                    controller.cancelOrder(order.getOrderId());
                    refreshOrders();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Cancellation Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            btns.add(cancelBtn);
        }

        card.add(btns, BorderLayout.EAST);
        card.add(Box.createVerticalStrut(6), BorderLayout.SOUTH);
        return card;
    }

    // ── Menu Management ──────────────────────────────────────────────────────

    private JPanel buildMenuTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JButton addBtn = UITheme.primaryButton("+ Add Item");
        JButton addonBtn = UITheme.secondaryButton("+ Add-on");
        JButton stockBtn = UITheme.secondaryButton("📦 Update Stock");
        JButton toggleBtn = UITheme.secondaryButton("Toggle Available");
        JButton deleteBtn = UITheme.dangerButton("Delete");

        toolbar.add(addBtn);
        toolbar.add(addonBtn);
        toolbar.add(stockBtn);
        toolbar.add(toggleBtn);
        toolbar.add(deleteBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Category", "Price (BDT)", "Available", "Stock"};
        menuModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        menuTable = new JTable(menuModel);
        menuTable.setFont(UITheme.FONT_BODY);
        menuTable.setRowHeight(26);
        menuTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(menuTable);
        panel.add(scroll, BorderLayout.CENTER);

        loadMenuTable();

        addBtn.addActionListener(e -> showAddItemDialog());
        addonBtn.addActionListener(e -> showAddAddonDialog());

        stockBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a menu item first.");
                return;
            }

            String itemId = (String) menuModel.getValueAt(row, 0);
            String itemName = (String) menuModel.getValueAt(row, 1);
            String currentStock = (String) menuModel.getValueAt(row, 5).toString();

            showUpdateStockDialog(itemId, itemName, currentStock);
        });

        toggleBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a menu item first.");
                return;
            }

            String itemId = (String) menuModel.getValueAt(row, 0);
            boolean current = (boolean) menuModel.getValueAt(row, 4);

            try {
                controller.toggleAvailability(itemId, current);
                loadMenuTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a menu item first.");
                return;
            }

            String itemId = (String) menuModel.getValueAt(row, 0);

            try {
                controller.deleteMenuItem(itemId);
                loadMenuTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void loadMenuTable() {
        menuModel.setRowCount(0);

        for (RestaurantMenuRowViewModel row : controller.loadMenuRows(restaurant.getId())) {
            menuModel.addRow(new Object[]{
                    row.getId(),
                    row.getName(),
                    row.getCategory(),
                    row.getPriceText(),
                    row.isAvailable(),
                    row.getStockText()
            });
        }
    }

    private void showAddItemDialog() {
        JTextField name = UITheme.textField(20);
        JTextField cat = UITheme.textField(20);
        JTextField price = UITheme.textField(10);
        JTextField desc = UITheme.textField(30);
        JTextField qty = UITheme.textField(10);
        qty.setText("-1");
        qty.setToolTipText("-1 = unlimited");

        Object[] fields = {
                "Name:", name,
                "Category:", cat,
                "Price (BDT):", price,
                "Description:", desc,
                "Stock (-1 = unlimited):", qty
        };

        int ok = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Menu Item",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            controller.addMenuItem(
                    restaurant.getId(),
                    new MenuItemForm(
                            name.getText(),
                            cat.getText(),
                            price.getText(),
                            desc.getText(),
                            qty.getText()
                    )
            );
            loadMenuTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Add Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showAddAddonDialog() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        String itemId = (String) menuModel.getValueAt(row, 0);

        JTextField name = UITheme.textField(20);
        JTextField price = UITheme.textField(10);
        price.setText("0");

        Object[] fields = {
                "Add-on Name:", name,
                "Extra Price (BDT):", price
        };

        int ok = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Add-on",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            controller.addAddon(
                    itemId,
                    new MenuAddonForm(name.getText(), price.getText())
            );
            JOptionPane.showMessageDialog(this, "Add-on added successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Add-on Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ── Settings ─────────────────────────────────────────────────────────────

    private JPanel buildSettingsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        RestaurantSettingsViewModel settings = controller.buildSettingsView(restaurant);

        JTextField nameField = UITheme.textField(24);
        nameField.setText(settings.getName());

        JTextField phoneField = UITheme.textField(24);
        phoneField.setText(settings.getPhone());

        JTextField minOrdFld = UITheme.textField(10);
        minOrdFld.setText(settings.getMinOrderText());

        JTextField etaFld = UITheme.textField(10);
        etaFld.setText(settings.getEtaText());

        JTextField openTime = UITheme.textField(10);
        openTime.setText(settings.getOpenTime());

        JTextField closeTime = UITheme.textField(10);
        closeTime.setText(settings.getCloseTime());

        JTextField descField = UITheme.textField(30);
        descField.setText(settings.getDescription());

        String[][] rows = {
                {"Restaurant Name:", null},
                {"Phone:", null},
                {"Min Order (BDT):", null},
                {"Estimated Delivery (min):", null},
                {"Open Time (HH:mm):", null},
                {"Close Time (HH:mm):", null},
                {"Description:", null}
        };

        Component[] fields = {
                nameField,
                phoneField,
                minOrdFld,
                etaFld,
                openTime,
                closeTime,
                descField
        };

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i * 2;
            addToGrid(panel, new JLabel(rows[i][0]), gc);

            gc.gridy = i * 2 + 1;
            addToGrid(panel, fields[i], gc);
        }

        gc.gridy = rows.length * 2;
        gc.insets = new Insets(12, 4, 4, 4);

        JPanel ratingCard = new JPanel(new BorderLayout());
        ratingCard.setBackground(new Color(0xFFF8E1, false));
        ratingCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.STAR_COLOR),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel ratingTitle = new JLabel("⭐ Overall Rating");
        ratingTitle.setFont(UITheme.FONT_BOLD);
        ratingTitle.setForeground(UITheme.STAR_COLOR);

        JLabel ratingValue = new JLabel(settings.getRatingText());
        ratingValue.setFont(UITheme.FONT_HEADING);
        ratingValue.setForeground(UITheme.TEXT_MAIN);

        ratingCard.add(ratingTitle, BorderLayout.NORTH);
        ratingCard.add(ratingValue, BorderLayout.CENTER);

        addToGrid(panel, ratingCard, gc);

        JButton save = UITheme.primaryButton("Save Settings");
        save.addActionListener(e -> {
            try {
                restaurant = controller.saveSettings(
                        restaurant,
                        new RestaurantSettingsForm(
                                nameField.getText(),
                                phoneField.getText(),
                                minOrdFld.getText(),
                                etaFld.getText(),
                                openTime.getText(),
                                closeTime.getText(),
                                descField.getText()
                        )
                );

                if (titleLabel != null) {
                    titleLabel.setText("🍽  " + restaurant.getName() + "  –  Owner Dashboard");
                }

                JOptionPane.showMessageDialog(this, "Settings saved!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Save Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        gc.gridy = rows.length * 2 + 2;
        gc.insets = new Insets(8, 4, 8, 4);
        addToGrid(panel, save, gc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);

        return wrapper;
    }

    private void addToGrid(JPanel panel, Component c, GridBagConstraints gc) {
        panel.add(c, gc);
    }

    private void showRegisterRestaurantDialog() {
        JTextField name = UITheme.textField(22);
        JTextField cuisine = UITheme.textField(22);
        JTextField street = UITheme.textField(22);
        JTextField area = UITheme.textField(22);
        JTextField phone = UITheme.textField(22);
        JTextField minOrd = UITheme.textField(10);
        minOrd.setText("150");
        JTextField eta = UITheme.textField(10);
        eta.setText("30");

        Object[] fields = {
                "Restaurant Name:", name,
                "Cuisine Type:", cuisine,
                "Street Address:", street,
                "Area:", area,
                "Phone:", phone,
                "Min Order (BDT):", minOrd,
                "Est. Delivery (min):", eta
        };

        int ok = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Register Your Restaurant",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok != JOptionPane.OK_OPTION) {
            controller.logout();
            onLogout.run();
            return;
        }

        try {
            restaurant = controller.registerRestaurant(
                    owner.getId(),
                    new RestaurantRegistrationForm(
                            name.getText(),
                            cuisine.getText(),
                            street.getText(),
                            area.getText(),
                            phone.getText(),
                            minOrd.getText(),
                            eta.getText()
                    )
            );

            buildUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            controller.logout();
            onLogout.run();
        }
    }

    private void showUpdateStockDialog(String itemId, String itemName, String currentStock) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.CARD_BG);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 4, 6, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JLabel nameLabel = new JLabel("Item: " + itemName);
        nameLabel.setFont(UITheme.FONT_BOLD);
        nameLabel.setForeground(UITheme.PRIMARY);

        JLabel currentLabel = new JLabel("Current stock: " + currentStock);
        currentLabel.setFont(UITheme.FONT_BODY);
        currentLabel.setForeground(UITheme.TEXT_MUTED);

        JTextField newStockField = UITheme.textField(10);
        newStockField.setToolTipText("Enter new stock amount (-1 = unlimited)");

        JLabel hint = UITheme.mutedLabel("Use -1 for unlimited stock");

        gc.gridy = 0;
        panel.add(nameLabel, gc);

        gc.gridy = 1;
        panel.add(currentLabel, gc);

        gc.gridy = 2;
        gc.insets = new Insets(12, 4, 2, 4);
        panel.add(new JLabel("New Stock:"), gc);

        gc.gridy = 3;
        gc.insets = new Insets(2, 4, 4, 4);
        panel.add(newStockField, gc);

        gc.gridy = 4;
        panel.add(hint, gc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Stock – " + itemName,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String input = newStockField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a stock value.",
                    "Missing Value",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int newStock = Integer.parseInt(input);
            controller.updateStock(itemId, newStock);
            loadMenuTable();

            String message = newStock == -1
                    ? itemName + " set to unlimited stock."
                    : itemName + " stock updated to " + newStock + ".";

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Stock Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel buildAllOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        hdr.setBackground(UITheme.BG);

        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> refreshAllOrders(panel));
        hdr.add(refresh, BorderLayout.EAST);

        panel.add(hdr, BorderLayout.NORTH);
        panel.add(buildAllOrdersContent(), BorderLayout.CENTER);

        return panel;
    }

    private void refreshAllOrders(JPanel panel) {
        if (panel.getComponentCount() > 1) {
            panel.remove(panel.getComponent(1));
        }

        panel.add(buildAllOrdersContent(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JScrollPane buildAllOrdersContent() {
        List<RestaurantOrderViewModel> allOrders = controller.loadAllOrders(restaurant.getId());

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UITheme.BG);
        list.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        if (allOrders.isEmpty()) {
            JLabel lbl = new JLabel("No orders yet.", SwingConstants.CENTER);
            lbl.setFont(UITheme.FONT_HEADING);
            lbl.setForeground(UITheme.TEXT_MUTED);
            list.add(lbl);
        } else {
            for (RestaurantOrderViewModel order : allOrders) {
                list.add(buildOrderCard(order));
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        return scroll;
    }

}