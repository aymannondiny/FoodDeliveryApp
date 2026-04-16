package com.fooddelivery.ui.customer.menu.viewmodel;

import com.fooddelivery.model.MenuItem;

public class MenuItemViewModel {

    private final MenuItem menuItem;
    private final String name;
    private final String description;
    private final String priceText;
    private final boolean orderable;

    public MenuItemViewModel(MenuItem menuItem,
                             String name,
                             String description,
                             String priceText,
                             boolean orderable) {
        this.menuItem = menuItem;
        this.name = name;
        this.description = description;
        this.priceText = priceText;
        this.orderable = orderable;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPriceText() {
        return priceText;
    }

    public boolean isOrderable() {
        return orderable;
    }
}