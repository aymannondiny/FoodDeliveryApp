package com.fooddelivery.service;

import com.fooddelivery.model.*;

import java.util.*;

/**
 * Manages the in-memory shopping cart for the current customer session.
 * A cart is tied to one restaurant at a time.
 */
public class CartService {

    private static CartService instance;

    private String            restaurantId;
    private String            restaurantName;
    private List<OrderItem>   items = new ArrayList<>();

    private CartService() {}

    public static synchronized CartService getInstance() {
        if (instance == null) instance = new CartService();
        return instance;
    }

    /** Add a menu item to the cart. Clears cart if adding from a different restaurant. */
    public void addItem(MenuItem menuItem, int quantity,
                        List<MenuItemAddon> selectedAddons, String specialInstructions) {
        if (!menuItem.getRestaurantId().equals(restaurantId)) {
            clear(); // Start fresh for new restaurant
            this.restaurantId = menuItem.getRestaurantId();
        }

        // Try to find an existing matching line (same item + same addons + same note)
        Optional<OrderItem> existing = items.stream()
            .filter(i -> i.getMenuItemId().equals(menuItem.getId())
                      && i.getSpecialInstructions() != null
                      && i.getSpecialInstructions().equals(specialInstructions))
            .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            OrderItem oi = new OrderItem(
                menuItem.getId(), menuItem.getName(),
                menuItem.getPrice(), quantity
            );
            oi.setSelectedAddons(selectedAddons != null ? selectedAddons : new ArrayList<>());
            oi.setSpecialInstructions(specialInstructions);
            items.add(oi);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) items.remove(index);
    }

    public void updateQuantity(int index, int newQuantity) {
        if (index >= 0 && index < items.size()) {
            if (newQuantity <= 0) items.remove(index);
            else items.get(index).setQuantity(newQuantity);
        }
    }

    public void clear() {
        items.clear();
        restaurantId   = null;
        restaurantName = null;
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    public int getTotalItems() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String          getRestaurantId()           { return restaurantId; }
    public void            setRestaurantId(String id)  { this.restaurantId = id; }
    public String          getRestaurantName()         { return restaurantName; }
    public void            setRestaurantName(String n) { this.restaurantName = n; }
    public List<OrderItem> getItems()                  { return Collections.unmodifiableList(items); }
}
