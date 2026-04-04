package com.fooddelivery.repository;

import com.fooddelivery.model.MenuItem;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class MenuItemRepository extends FileRepository<MenuItem> {
    private static MenuItemRepository instance;

    private MenuItemRepository() {
        super("data/menu_items.json", new TypeToken<Map<String, MenuItem>>(){}.getType());
    }

    public static synchronized MenuItemRepository getInstance() {
        if (instance == null) instance = new MenuItemRepository();
        return instance;
    }

    public List<MenuItem> findByRestaurant(String restaurantId) {
        return findWhere(m -> restaurantId.equals(m.getRestaurantId()));
    }
}
