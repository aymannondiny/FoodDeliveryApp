package com.fooddelivery.ui.customer;

import com.fooddelivery.model.*;
import com.fooddelivery.service.AuthService;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.CouponService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Displays the current shopping cart, coupon input, and checkout form.
 */
public class CartPanel extends JPanel {

    private final Consumer<Order> onOrderPlaced;
    private JPanel   itemsPanel;
    private JLabel   subtotalLabel;
    private JLabel   discountLabel;
    private JLabel   deliveryFeeLabel;
    private JLabel   totalLabel;
    private JTextField couponField;
    private double   appliedDiscount = 0;
    private String   appliedCouponCode = null;

    public CartPanel(Consumer<Order> onOrderPlaced) {
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

        // Items list
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(UITheme.BG);
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Summary + checkout (south)
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(UITheme.CARD_BG);
        south.setBorder(UITheme.cardBorder());

        subtotalLabel    = makeRow(south, "Subtotal:",    "0.00 BDT");
        discountLabel    = makeRow(south, "Discount:",    "-0.00 BDT");
        deliveryFeeLabel = makeRow(south, "Delivery fee:","30.00 BDT");
        south.add(UITheme.separator());
        totalLabel       = makeRow(south, "Total:",       "0.00 BDT");
        totalLabel.setFont(UITheme.FONT_HEADING);
        totalLabel.setForeground(UITheme.PRIMARY);

        // Coupon
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

        // Checkout button
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
        CartService cart = CartService.getInstance();

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

            for (int i = 0; i < cart.getItems().size(); i++) {
                itemsPanel.add(buildItemRow(i, cart.getItems().get(i)));
                itemsPanel.add(Box.createVerticalStrut(4));
            }
        }
        updateTotals();
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel buildItemRow(int index, OrderItem item) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(UITheme.CARD_BG);
        row.setBorder(UITheme.cardBorder());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(new JLabel(item.getMenuItemName()));
        JLabel price = UITheme.mutedLabel(String.format("%.2f BDT each", item.getUnitPrice()));
        info.add(price);
        row.add(info, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        controls.setOpaque(false);
        JButton minus = new JButton("-");
        JLabel  qty   = new JLabel(String.valueOf(item.getQuantity()));
        JButton plus  = new JButton("+");
        JButton remove = UITheme.dangerButton("✕");
        remove.setFont(UITheme.FONT_SMALL);
        remove.setPreferredSize(new Dimension(28, 28));

        minus.addActionListener(e -> { CartService.getInstance().updateQuantity(index, item.getQuantity()-1); refresh(); });
        plus.addActionListener(e  -> { CartService.getInstance().updateQuantity(index, item.getQuantity()+1); refresh(); });
        remove.addActionListener(e-> { CartService.getInstance().removeItem(index); refresh(); });

        controls.add(minus); controls.add(qty); controls.add(plus); controls.add(remove);

        JLabel total = new JLabel(String.format("%.2f BDT", item.getLineTotal()));
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
        if (code.isEmpty()) return;
        try {
            double subtotal  = CartService.getInstance().getSubtotal();
            Coupon coupon    = CouponService.getInstance().validateCoupon(code, subtotal);
            appliedDiscount  = coupon.calculateDiscount(subtotal);
            appliedCouponCode = code;
            JOptionPane.showMessageDialog(this,
                String.format("Coupon applied! You save %.2f BDT", appliedDiscount),
                "Coupon Applied", JOptionPane.INFORMATION_MESSAGE);
            updateTotals();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Invalid Coupon", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTotals() {
        CartService cart   = CartService.getInstance();
        double subtotal    = cart.getSubtotal();
        double deliveryFee = 30.0; // Default; real calc done at order time
        double total       = subtotal + deliveryFee - appliedDiscount;

        subtotalLabel.setText(String.format("%.2f BDT", subtotal));
        discountLabel.setText(String.format("-%.2f BDT", appliedDiscount));
        deliveryFeeLabel.setText(String.format("%.2f BDT", deliveryFee));
        totalLabel.setText(String.format("%.2f BDT", total));
    }

    private void showCheckoutDialog() {
        if (CartService.getInstance().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     "Checkout", true);
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(UITheme.CARD_BG);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        gc.insets = new Insets(6, 4, 6, 4);

        User user = AuthService.getInstance().requireCurrentUser();
        String defaultArea = (user.getDefaultAddress() != null)
            ? user.getDefaultAddress().getArea() : "";

        JTextField streetField = UITheme.textField(24);
        JTextField areaField   = UITheme.textField(24);
        areaField.setText(defaultArea);
        JTextField cityField   = UITheme.textField(24);
        cityField.setText("Dhaka");

        JComboBox<Order.PaymentMethod> pmBox =
            new JComboBox<>(Order.PaymentMethod.values());
        pmBox.setFont(UITheme.FONT_BODY);

        JTextField noteField = UITheme.textField(24);

        String[][] rows = {{"Street / House No:", null}, {"Area:", null},
                           {"City:", null}, {"Payment Method:", null}, {"Note:", null}};
        Component[] fields = {streetField, areaField, cityField, pmBox, noteField};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i * 2;     gc.insets = new Insets(4, 4, 0, 4);
            form.add(new JLabel(rows[i][0]), gc);
            gc.gridy = i * 2 + 1; gc.insets = new Insets(0, 4, 6, 4);
            form.add(fields[i], gc);
        }
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton cancel  = UITheme.secondaryButton("Cancel");
        JButton confirm = UITheme.primaryButton("Place Order");
        cancel.addActionListener(e -> dialog.dispose());
        confirm.addActionListener(e -> {
            try {
                Address addr = new Address(streetField.getText(), areaField.getText(),
                                           cityField.getText(), "");
                String restaurantId = CartService.getInstance().getRestaurantId();
                Restaurant restaurant = RestaurantService.getInstance()
                    .findById(restaurantId)
                    .orElseThrow(() -> new IllegalStateException("Restaurant not found."));

                Order order = OrderService.getInstance().placeOrder(
                    user.getId(), restaurant, addr,
                    (Order.PaymentMethod) pmBox.getSelectedItem(),
                    appliedCouponCode
                );

                appliedDiscount   = 0;
                appliedCouponCode = null;
                couponField.setText("");
                dialog.dispose();
                onOrderPlaced.accept(order);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                    "Order Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnRow.add(cancel);
        btnRow.add(confirm);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
