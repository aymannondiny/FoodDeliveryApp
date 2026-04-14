package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single item on a restaurant's menu.
 * Supports add-ons, quantity tracking, and availability toggling.
 */
public class MenuItem {
    private String           id;
    private String           restaurantId;
    private String           name;
    private String           description;
    private String           category;    // e.g. "Starters", "Mains", "Drinks"
    private double           price;
    private boolean          available;
    private int              quantity;    // -1 = unlimited
    private List<MenuItemAddon> addons;
    private String           imageUrl;

    public MenuItem() {
        this.available = true;
        this.quantity  = -1;
        this.addons    = new ArrayList<>();
    }

    public MenuItem(String id, String restaurantId, String name,
                    String description, String category, double price) {
        this();
        this.id           = id;
        this.restaurantId = restaurantId;
        this.name         = name;
        this.description  = description;
        this.category     = category;
        this.price        = price;
    }

    public boolean isOrderable() {
        return available && (quantity == -1 || quantity > 0);
    }

    // Decrements stock if quantity-tracked; no-op if unlimited.
    public void decrementQuantity(int amount) {
        if (quantity != -1) quantity = Math.max(0, quantity - amount);
    }

    public void addAddon(MenuItemAddon addon) { addons.add(addon); }

    @Override
    public String toString() {
        return String.format("[%s] %s – %.2f BDT", category, name, price);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String  getId()                           { return id; }
    public void    setId(String id)                  { this.id = id; }
    public String  getRestaurantId()                 { return restaurantId; }
    public void    setRestaurantId(String rid)       { this.restaurantId = rid; }
    public String  getName()                         { return name; }
    public void    setName(String n)                 { this.name = n; }
    public String  getDescription()                  { return description; }
    public void    setDescription(String d)          { this.description = d; }
    public String  getCategory()                     { return category; }
    public void    setCategory(String c)             { this.category = c; }
    public double  getPrice()                        { return price; }
    public void    setPrice(double p)                { this.price = p; }
    public boolean isAvailable()                     { return available; }
    public void    setAvailable(boolean a)           { this.available = a; }
    public int     getQuantity()                     { return quantity; }
    public void    setQuantity(int q)                { this.quantity = q; }
    public List<MenuItemAddon> getAddons()           { return addons; }
    public void    setAddons(List<MenuItemAddon> a)  { this.addons = a; }
    public String  getImageUrl()                     { return imageUrl; }
    public void    setImageUrl(String url)           { this.imageUrl = url; }
}
