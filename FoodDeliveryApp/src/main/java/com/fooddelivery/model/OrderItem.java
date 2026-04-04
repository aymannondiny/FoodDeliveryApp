package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A single line in an order: one menu item with chosen quantity,
 * selected add-ons, and any special instructions.
 */
public class OrderItem {
    private String             menuItemId;
    private String             menuItemName;   // Snapshot at order time
    private double             unitPrice;      // Snapshot at order time
    private int                quantity;
    private List<MenuItemAddon> selectedAddons;
    private String             specialInstructions;

    public OrderItem() {
        this.selectedAddons = new ArrayList<>();
    }

    public OrderItem(String menuItemId, String menuItemName,
                     double unitPrice, int quantity) {
        this();
        this.menuItemId   = menuItemId;
        this.menuItemName = menuItemName;
        this.unitPrice    = unitPrice;
        this.quantity     = quantity;
    }

    /** Total price for this line including all add-ons, multiplied by quantity. */
    public double getLineTotal() {
        double addonsTotal = selectedAddons.stream()
                                           .mapToDouble(MenuItemAddon::getExtraPrice)
                                           .sum();
        return (unitPrice + addonsTotal) * quantity;
    }

    @Override
    public String toString() {
        return String.format("%d× %s = %.2f BDT", quantity, menuItemName, getLineTotal());
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String  getMenuItemId()                        { return menuItemId; }
    public void    setMenuItemId(String id)               { this.menuItemId = id; }
    public String  getMenuItemName()                      { return menuItemName; }
    public void    setMenuItemName(String n)              { this.menuItemName = n; }
    public double  getUnitPrice()                         { return unitPrice; }
    public void    setUnitPrice(double p)                 { this.unitPrice = p; }
    public int     getQuantity()                          { return quantity; }
    public void    setQuantity(int q)                     { this.quantity = q; }
    public List<MenuItemAddon> getSelectedAddons()        { return selectedAddons; }
    public void    setSelectedAddons(List<MenuItemAddon> a){ this.selectedAddons = a; }
    public String  getSpecialInstructions()               { return specialInstructions; }
    public void    setSpecialInstructions(String s)       { this.specialInstructions = s; }
}
