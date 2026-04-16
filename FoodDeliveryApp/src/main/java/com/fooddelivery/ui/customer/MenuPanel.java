package com.fooddelivery.ui.customer;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.customer.menu.MenuController;
import com.fooddelivery.ui.customer.menu.viewmodel.MenuCategoryViewModel;
import com.fooddelivery.ui.customer.menu.viewmodel.MenuItemViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Shows a restaurant's full menu grouped by category.
 * Delegates loading and cart orchestration to MenuController.
 */
public class MenuPanel extends JPanel {

    private final Restaurant restaurant;
    private final MenuController controller;
    private final Consumer<Void> onCartChanged;
    private final Runnable onBack;

    private JPanel contentPanel;

    public MenuPanel(Restaurant restaurant,
                     MenuController controller,
                     Consumer<Void> onCartChanged,
                     Runnable onBack) {
        this.restaurant = restaurant;
        this.controller = controller;
        this.onCartChanged = onCartChanged;
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(UITheme.SECONDARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JButton backBtn = UITheme.secondaryButton("← Back");
        backBtn.setBackground(UITheme.SECONDARY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> onBack.run());

        JLabel nameLabel = new JLabel(restaurant.getName());
        nameLabel.setFont(UITheme.FONT_TITLE);
        nameLabel.setForeground(Color.WHITE);

        JLabel ratingLabel = new JLabel(
                UITheme.starRating(restaurant.getRating())
                        + "  ·  " + restaurant.getCuisineType()
                        + "  ·  " + restaurant.getEstimatedDeliveryMinutes() + " min"
        );
        ratingLabel.setFont(UITheme.FONT_SMALL);
        ratingLabel.setForeground(new Color(0xBDC3C7, false));

        JPanel titleArea = new JPanel(new GridLayout(2, 1));
        titleArea.setOpaque(false);
        titleArea.add(nameLabel);
        titleArea.add(ratingLabel);

        header.add(backBtn, BorderLayout.WEST);
        header.add(titleArea, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        loadMenu();
    }

    private void loadMenu() {
        contentPanel.removeAll();
        List<MenuCategoryViewModel> categories = controller.loadMenu(restaurant.getId());

        if (categories.isEmpty()) {
            JLabel empty = new JLabel("No menu items available.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_HEADING);
            empty.setForeground(UITheme.TEXT_MUTED);
            contentPanel.add(empty);
        } else {
            for (MenuCategoryViewModel category : categories) {
                JLabel catLabel = new JLabel("  " + category.getCategoryName());
                catLabel.setFont(UITheme.FONT_HEADING);
                catLabel.setForeground(UITheme.PRIMARY);
                catLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY));
                catLabel.setAlignmentX(LEFT_ALIGNMENT);

                contentPanel.add(Box.createVerticalStrut(12));
                contentPanel.add(catLabel);
                contentPanel.add(Box.createVerticalStrut(6));

                for (MenuItemViewModel item : category.getItems()) {
                    contentPanel.add(buildItemCard(item));
                    contentPanel.add(Box.createVerticalStrut(6));
                }
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildItemCard(MenuItemViewModel vm) {
        MenuItem item = vm.getMenuItem();

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(vm.getName());
        nameLabel.setFont(UITheme.FONT_BOLD);
        nameLabel.setForeground(vm.isOrderable() ? UITheme.TEXT_MAIN : UITheme.TEXT_MUTED);

        String desc = vm.getDescription();
        JLabel descLabel = UITheme.mutedLabel(
                desc.length() > 70 ? desc.substring(0, 67) + "…" : desc
        );

        JLabel priceLabel = new JLabel(vm.getPriceText());
        priceLabel.setFont(UITheme.FONT_BOLD);
        priceLabel.setForeground(UITheme.PRIMARY);

        info.add(nameLabel);
        info.add(descLabel);
        info.add(priceLabel);
        card.add(info, BorderLayout.CENTER);

        if (vm.isOrderable()) {
            JButton addBtn = UITheme.primaryButton("+ Add");
            addBtn.setPreferredSize(new Dimension(90, 34));
            addBtn.addActionListener(e -> showAddDialog(item));
            card.add(addBtn, BorderLayout.EAST);
        } else {
            JLabel unavailable = new JLabel("Unavailable");
            unavailable.setFont(UITheme.FONT_SMALL);
            unavailable.setForeground(UITheme.DANGER);
            card.add(unavailable, BorderLayout.EAST);
        }

        return card;
    }

    private void showAddDialog(MenuItem item) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add " + item.getName(),
                true
        );
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        panel.setBackground(UITheme.CARD_BG);

        JPanel qtyRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        qtyRow.setOpaque(false);
        qtyRow.add(new JLabel("Quantity: "));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
                1,
                1,
                item.getQuantity() == -1 ? 99 : item.getQuantity(),
                1
        );
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setFont(UITheme.FONT_BODY);
        qtyRow.add(spinner);
        panel.add(qtyRow);

        List<JCheckBox> addonBoxes = new ArrayList<>();
        List<MenuItemAddon> addons = item.getAddons() != null ? item.getAddons() : List.of();

        if (!addons.isEmpty()) {
            panel.add(new JLabel("Add-ons:"));
            for (MenuItemAddon addon : addons) {
                if (addon.isAvailable()) {
                    JCheckBox cb = new JCheckBox(addon.toString());
                    cb.setFont(UITheme.FONT_BODY);
                    cb.setOpaque(false);
                    cb.putClientProperty("addon", addon);
                    addonBoxes.add(cb);
                    panel.add(cb);
                }
            }
        }

        panel.add(Box.createVerticalStrut(8));
        panel.add(new JLabel("Special instructions (optional):"));
        JTextField instrField = UITheme.textField(30);
        panel.add(instrField);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton cancel = UITheme.secondaryButton("Cancel");
        JButton add = UITheme.primaryButton("Add to Cart");

        cancel.addActionListener(e -> dialog.dispose());
        add.addActionListener(e -> {
            List<MenuItemAddon> selected = new ArrayList<>();
            for (JCheckBox cb : addonBoxes) {
                if (cb.isSelected()) {
                    selected.add((MenuItemAddon) cb.getClientProperty("addon"));
                }
            }

            if (controller.hasItemsFromAnotherRestaurant(restaurant.getId())) {
                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Your cart contains items from another restaurant.\nClear cart and add this item?",
                        "Clear Cart?",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            controller.addToCart(
                    restaurant,
                    item,
                    (int) spinner.getValue(),
                    selected,
                    instrField.getText()
            );

            onCartChanged.accept(null);
            dialog.dispose();

            JOptionPane.showMessageDialog(
                    this,
                    item.getName() + " added to cart!",
                    "Added",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        btnRow.add(cancel);
        btnRow.add(add);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}