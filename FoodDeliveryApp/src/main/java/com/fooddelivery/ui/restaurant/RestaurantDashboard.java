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

        titleLabel = new JLabel("🍽  " + restaurant.getName() + "  –  Owner Dashboard");
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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel idLabel = new JLabel("Order #" + order.getOrderId());
        idLabel.setFont(UITheme.FONT_BOLD);

        JLabel itemsLabel = UITheme.mutedLabel(order.getItemsSummary());

        JLabel totalLabel = new JLabel(order.getTotalText());
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.PRIMARY);

        JLabel statusLabel = new JLabel(order.getStatusDescription());
        statusLabel.setForeground(UITheme.SUCCESS);

        JPanel info = new JPanel(new GridLayout(4, 1, 0, 2));
        info.setOpaque(false);
        info.add(idLabel);
        info.add(itemsLabel);
        info.add(totalLabel);
        info.add(statusLabel);
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
        JButton toggleBtn = UITheme.secondaryButton("Toggle Available");
        JButton deleteBtn = UITheme.dangerButton("Delete");

        toolbar.add(addBtn);
        toolbar.add(addonBtn);
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

        gc.gridy = rows.length * 2 + 1;
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
}