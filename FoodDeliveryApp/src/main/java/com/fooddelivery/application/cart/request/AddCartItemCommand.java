package com.fooddelivery.application.cart.request;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;

import java.util.List;

public class AddCartItemCommand {

    private final MenuItem menuItem;
    private final int quantity;
    private final List<MenuItemAddon> selectedAddons;
    private final String specialInstructions;
    private final String restaurantName;

    public AddCartItemCommand(MenuItem menuItem,
                              int quantity,
                              List<MenuItemAddon> selectedAddons,
                              String specialInstructions,
                              String restaurantName) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedAddons = selectedAddons;
        this.specialInstructions = specialInstructions;
        this.restaurantName = restaurantName;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<MenuItemAddon> getSelectedAddons() {
        return selectedAddons;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
}