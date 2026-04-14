package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User-scoped shopping cart.
 * A cart can contain items from only one restaurant at a time.
 */
public class Cart {

    private String ownerId;
    private String restaurantId;
    private String restaurantName;
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(String ownerId) {
        this();
        this.ownerId = ownerId;
    }

    public void addItem(MenuItem menuItem,
                        int quantity,
                        List<MenuItemAddon> selectedAddons,
                        String specialInstructions,
                        String incomingRestaurantName) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (restaurantId != null && !restaurantId.equals(menuItem.getRestaurantId())) {
            clear();
        }

        if (restaurantId == null) {
            restaurantId = menuItem.getRestaurantId();
        }

        if (incomingRestaurantName != null && !incomingRestaurantName.isBlank()) {
            restaurantName = incomingRestaurantName;
        }

        CartItem existing = items.stream()
                .filter(i -> i.matches(menuItem, selectedAddons, specialInstructions))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            return;
        }

        CartItem item = new CartItem(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getPrice(),
                quantity
        );
        item.setSelectedAddons(selectedAddons);
        item.setSpecialInstructions(specialInstructions);
        items.add(item);
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            resetRestaurantIfEmpty();
        }
    }

    public void updateQuantity(int index, int newQuantity) {
        if (index < 0 || index >= items.size()) {
            return;
        }

        if (newQuantity <= 0) {
            items.remove(index);
        } else {
            items.get(index).setQuantity(newQuantity);
        }

        resetRestaurantIfEmpty();
    }

    public void clear() {
        items.clear();
        restaurantId = null;
        restaurantName = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    private void resetRestaurantIfEmpty() {
        if (items.isEmpty()) {
            restaurantId = null;
            restaurantName = null;
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<CartItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }
}