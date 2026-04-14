package com.fooddelivery.ui.customer.menu.viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuCategoryViewModel {

    private final String categoryName;
    private final List<MenuItemViewModel> items;

    public MenuCategoryViewModel(String categoryName, List<MenuItemViewModel> items) {
        this.categoryName = categoryName;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<MenuItemViewModel> getItems() {
        return Collections.unmodifiableList(items);
    }
}