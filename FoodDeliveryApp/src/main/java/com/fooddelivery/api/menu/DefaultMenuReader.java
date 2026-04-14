package com.fooddelivery.api.menu;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.service.MenuService;

import java.util.List;
import java.util.Map;

public class DefaultMenuReader implements MenuReader {

    private final MenuService menuService;

    public DefaultMenuReader(MenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public Map<String, List<MenuItem>> getMenuByCategory(String restaurantId) {
        return menuService.getMenuByCategory(restaurantId);
    }
}