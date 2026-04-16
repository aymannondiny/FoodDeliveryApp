package com.fooddelivery.service;

import com.fooddelivery.application.menu.MenuManagementService;
import com.fooddelivery.application.menu.MenuQueryService;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer menu application services from AppContext.
 */
@Deprecated
public class MenuService {

    private static MenuService instance;

    private final MenuQueryService queryService;
    private final MenuManagementService managementService;

    private MenuService() {
        AppContext context = AppContext.create();
        this.queryService = context.menuQueryService();
        this.managementService = context.menuManagementService();
    }

    public static synchronized MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }

    public MenuItem addMenuItem(String restaurantId, String name, String description,
                                String category, double price) {
        return managementService.addMenuItem(restaurantId, name, description, category, price);
    }

    public MenuItemAddon addAddon(String menuItemId, String name, double extraPrice) {
        return managementService.addAddon(menuItemId, name, extraPrice);
    }

    public List<MenuItem> getMenuForRestaurant(String restaurantId) {
        return queryService.getMenuForRestaurant(restaurantId);
    }

    public List<MenuItem> getAvailableMenu(String restaurantId) {
        return queryService.getAvailableMenu(restaurantId);
    }

    public Map<String, List<MenuItem>> getMenuByCategory(String restaurantId) {
        return queryService.getMenuByCategory(restaurantId);
    }

    public List<MenuItem> searchMenu(String restaurantId, String query) {
        return queryService.searchMenu(restaurantId, query);
    }

    public Optional<MenuItem> findById(String id) {
        return queryService.findById(id);
    }

    public void setAvailability(String menuItemId, boolean available) {
        managementService.setAvailability(menuItemId, available);
    }

    public void updateQuantity(String menuItemId, int quantity) {
        managementService.updateQuantity(menuItemId, quantity);
    }

    public void updateItem(MenuItem item) {
        managementService.updateItem(item);
    }

    public void deleteItem(String menuItemId) {
        managementService.deleteItem(menuItemId);
    }

    public void decrementStock(String menuItemId, int quantity) {
        managementService.decrementStock(menuItemId, quantity);
    }
}