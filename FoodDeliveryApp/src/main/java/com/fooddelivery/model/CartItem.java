package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A single item in a shopping cart before checkout.
 */
public class CartItem {

    private String menuItemId;
    private String menuItemName;
    private double unitPrice;
    private int quantity;
    private List<MenuItemAddon> selectedAddons;
    private String specialInstructions;

    public CartItem() {
        this.selectedAddons = new ArrayList<>();
    }

    public CartItem(String menuItemId, String menuItemName, double unitPrice, int quantity) {
        this();
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public boolean matches(MenuItem menuItem,
                           List<MenuItemAddon> addons,
                           String instructions) {
        return Objects.equals(menuItemId, menuItem.getId())
                && Objects.equals(normalize(specialInstructions), normalize(instructions))
                && Objects.equals(addonKey(selectedAddons), addonKey(addons));
    }

    public double getLineTotal() {
        double addonsTotal = selectedAddons.stream()
                .mapToDouble(MenuItemAddon::getExtraPrice)
                .sum();
        return (unitPrice + addonsTotal) * quantity;
    }

    public OrderItem toOrderItem() {
        OrderItem orderItem = new OrderItem(menuItemId, menuItemName, unitPrice, quantity);
        orderItem.setSelectedAddons(new ArrayList<>(selectedAddons));
        orderItem.setSpecialInstructions(specialInstructions);
        return orderItem;
    }

    private String addonKey(List<MenuItemAddon> addons) {
        if (addons == null || addons.isEmpty()) {
            return "";
        }

        return addons.stream()
                .map(a -> a.getId() == null ? a.getName() : a.getId())
                .sorted()
                .collect(Collectors.joining("|"));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<MenuItemAddon> getSelectedAddons() {
        return Collections.unmodifiableList(selectedAddons);
    }

    public void setSelectedAddons(List<MenuItemAddon> selectedAddons) {
        this.selectedAddons = selectedAddons != null
                ? new ArrayList<>(selectedAddons)
                : new ArrayList<>();
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}