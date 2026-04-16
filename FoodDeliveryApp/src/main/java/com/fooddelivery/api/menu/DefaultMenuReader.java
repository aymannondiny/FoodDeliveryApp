package com.fooddelivery.api.menu;

import com.fooddelivery.application.menu.MenuQueryService;
import com.fooddelivery.model.MenuItem;

import java.util.List;
import java.util.Map;

public class DefaultMenuReader implements MenuReader {

    private final MenuQueryService menuQueryService;

    public DefaultMenuReader(MenuQueryService menuQueryService) {
        this.menuQueryService = menuQueryService;
    }

    @Override
    public Map<String, List<MenuItem>> getMenuByCategory(String restaurantId) {
        return menuQueryService.getMenuByCategory(restaurantId);
    }
}