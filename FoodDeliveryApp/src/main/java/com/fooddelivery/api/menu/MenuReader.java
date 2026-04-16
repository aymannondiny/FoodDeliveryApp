package com.fooddelivery.api.menu;

import com.fooddelivery.model.MenuItem;

import java.util.List;
import java.util.Map;

public interface MenuReader {
    Map<String, List<MenuItem>> getMenuByCategory(String restaurantId);
}