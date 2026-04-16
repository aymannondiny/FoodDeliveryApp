package com.fooddelivery.ui.customer;

import com.fooddelivery.model.Order;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.customer.cart.CartController;
import com.fooddelivery.ui.customer.cart.request.CheckoutForm;
import com.fooddelivery.ui.customer.cart.viewmodel.CartItemViewModel;
import com.fooddelivery.ui.customer.cart.viewmodel.CartViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Displays the current shopping cart, coupon input, and checkout form.
 * Delegates cart/order orchestration to CartController.
 */
public class CartPanel extends JPanel {

    private static final double PREVIEW_DELIVERY_FEE = 30.0;

    private final CartController controller;
    private final Consumer<Order> onOrderPlaced;

    private JPanel itemsPanel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel deliveryFeeLabel;
    private JLabel totalLabel;
    private JTextField couponField;

    private double appliedDiscount = 0;
    private String appliedCouponCode = null;

    public CartPanel(CartController controller, Consumer<Order> onOrderPlaced) {
        this.controller = controller;
        this.onOrderPlaced = onOrderPlaced;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        buildUI();
    }

    private void buildUI() {
        JLabel title = new JLabel("🛒  Your Cart");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.SECONDARY);
        add(title, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(UITheme.BG);

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(UITheme.CARD_BG);
        south.setBorder(UITheme.cardBorder());

        subtotalLabel = makeRow(south, "Subtotal:", "0.00 BDT");
        discountLabel = makeRow(south, "Discount:", "-0.00 BDT");
        deliveryFeeLabel = makeRow(south, "Delivery fee:", "30.00 BDT");

        south.add(UITheme.separator());

        totalLabel = makeRow(south, "Total:", "0.00 BDT");
        totalLabel.setFont(UITheme.FONT_HEADING);
        totalLabel.setForeground(UITheme.PRIMARY);

        south.add(Box.createVerticalStrut(8));

        JPanel couponRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        couponRow.setOpaque(false);

        couponField = UITheme.textField(14);
        couponField.setToolTipText("Enter coupon code");

        JButton applyBtn = UITheme.secondaryButton("Apply");
        applyBtn.addActionListener(e -> applyCoupon());

        couponRow.add(new JLabel("Coupon:"));
        couponRow.add(couponField);
        couponRow.add(applyBtn);
        south.add(couponRow);

        south.add(Box.createVerticalStrut(10));

        JButton checkoutBtn = UITheme.primaryButton("Proceed to Checkout →");
        checkoutBtn.setAlignmentX(CENTER_ALIGNMENT);
        checkoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        checkoutBtn.addActionListener(e -> showCheckoutDialog());
        south.add(checkoutBtn);

        add(south, BorderLayout.SOUTH);
        refresh();
    }

    private JLabel makeRow(JPanel parent, String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel key = new JLabel(labelText);
        key.setFont(UITheme.FONT_BODY);

        JLabel val = new JLabel(valueText);
        val.setFont(UITheme.FONT_BODY);
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);

        return val;
    }

    public void refresh() {
        itemsPanel.removeAll();

        CartViewModel cart = controller.loadCartView();
        syncAppliedCouponSilently();

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_HEADING);
            empty.setForeground(UITheme.TEXT_MUTED);
            itemsPanel.add(empty);
        } else {
            JLabel restaurant = new JLabel("From: " + cart.getRestaurantName());
            restaurant.setFont(UITheme.FONT_BOLD);
            restaurant.setForeground(UITheme.PRIMARY);
            restaurant.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            itemsPanel.add(restaurant);

            for (CartItemViewModel item : cart.getItems()) {
                itemsPanel.add(buildItemRow(item));
                itemsPanel.add(Box.createVerticalStrut(4));
            }
        }

        updateTotals();
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel buildItemRow(CartItemViewModel item) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(UITheme.CARD_BG);
        row.setBorder(UITheme.cardBorder());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(new JLabel(item.getName()));
        info.add(UITheme.mutedLabel(item.getUnitPriceText()));
        row.add(info, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        controls.setOpaque(false);

        JButton minus = new JButton("-");
        JLabel qty = new JLabel(String.valueOf(item.getQuantity()));
        JButton plus = new JButton("+");
        JButton remove = UITheme.dangerButton("✕");

        remove.setFont(UITheme.FONT_SMALL);
        remove.setPreferredSize(new Dimension(28, 28));

        minus.addActionListener(e -> {
            controller.updateQuantity(item.getIndex(), item.getQuantity() - 1);
            refresh();
        });

        plus.addActionListener(e -> {
            controller.updateQuantity(item.getIndex(), item.getQuantity() + 1);
            refresh();
        });

        remove.addActionListener(e -> {
            controller.removeItem(item.getIndex());
            refresh();
        });

        controls.add(minus);
        controls.add(qty);
        controls.add(plus);
        controls.add(remove);

        JLabel total = new JLabel(item.getLineTotalText());
        total.setFont(UITheme.FONT_BOLD);
        total.setForeground(UITheme.PRIMARY);

        JPanel right = new JPanel(new GridLayout(2, 1));
        right.setOpaque(false);
        right.add(total);
        right.add(controls);

        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void applyCoupon() {
        String code = couponField.getText().trim();
        if (code.isEmpty()) {
            return;
        }

        try {
            appliedDiscount = controller.previewCouponDiscount(code);
            appliedCouponCode = code;

            JOptionPane.showMessageDialog(
                    this,
                    String.format("Coupon applied! You save %.2f BDT", appliedDiscount),
                    "Coupon Applied",
                    JOptionPane.INFORMATION_MESSAGE
            );

            updateTotals();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid Coupon",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void syncAppliedCouponSilently() {
        if (appliedCouponCode == null || appliedCouponCode.isBlank()) {
            return;
        }

        try {
            appliedDiscount = controller.previewCouponDiscount(appliedCouponCode);
        } catch (Exception ignored) {
            appliedDiscount = 0;
            appliedCouponCode = null;
            if (couponField != null) {
                couponField.setText("");
            }
        }
    }

    private void updateTotals() {
        CartViewModel cart = controller.loadCartView();

        double subtotal = cart.getSubtotal();
        double total = subtotal + PREVIEW_DELIVERY_FEE - appliedDiscount;

        subtotalLabel.setText(String.format("%.2f BDT", subtotal));
        discountLabel.setText(String.format("-%.2f BDT", appliedDiscount));
        deliveryFeeLabel.setText(String.format("%.2f BDT", PREVIEW_DELIVERY_FEE));
        totalLabel.setText(String.format("%.2f BDT", total));
    }

    private void showCheckoutDialog() {
        CartViewModel cart = controller.loadCartView();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Checkout",
                true
        );
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(UITheme.CARD_BG);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.insets = new Insets(6, 4, 6, 4);

        JTextField streetField = UITheme.textField(24);
        JTextField areaField = UITheme.textField(24);
        areaField.setText(controller.getDefaultArea());

        JTextField cityField = UITheme.textField(24);
        cityField.setText("Dhaka");

        JComboBox<Order.PaymentMethod> pmBox =
                new JComboBox<>(Order.PaymentMethod.values());
        pmBox.setFont(UITheme.FONT_BODY);

        JTextField noteField = UITheme.textField(24);

        String[][] rows = {
                {"Street / House No:", null},
                {"Area:", null},
                {"City:", null},
                {"Payment Method:", null},
                {"Note:", null}
        };
        Component[] fields = {streetField, areaField, cityField, pmBox, noteField};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i * 2;
            gc.insets = new Insets(4, 4, 0, 4);
            form.add(new JLabel(rows[i][0]), gc);

            gc.gridy = i * 2 + 1;
            gc.insets = new Insets(0, 4, 6, 4);
            form.add(fields[i], gc);
        }

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton cancel = UITheme.secondaryButton("Cancel");
        JButton confirm = UITheme.primaryButton("Place Order");

        cancel.addActionListener(e -> dialog.dispose());

        confirm.addActionListener(e -> {
            try {
                Order order = controller.placeOrder(
                        new CheckoutForm(
                                streetField.getText(),
                                areaField.getText(),
                                cityField.getText(),
                                (Order.PaymentMethod) pmBox.getSelectedItem(),
                                noteField.getText()
                        ),
                        appliedCouponCode
                );

                appliedDiscount = 0;
                appliedCouponCode = null;
                couponField.setText("");

                dialog.dispose();
                onOrderPlaced.accept(order);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        ex.getMessage(),
                        "Order Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnRow.add(cancel);
        btnRow.add(confirm);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}