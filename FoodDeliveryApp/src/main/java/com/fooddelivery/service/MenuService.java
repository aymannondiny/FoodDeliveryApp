package com.fooddelivery.service;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages menu items: creation, availability toggling,
 * category grouping, searching, and add-on management.
 */
public class MenuService {

    private static MenuService instance;

    private MenuService() {}

    public static synchronized MenuService getInstance() {
        if (instance == null) instance = new MenuService();
        return instance;
    }

    // ── Creation ─────────────────────────────────────────────────────────────

    public MenuItem addMenuItem(String restaurantId, String name, String description,
                                String category, double price) {
        MenuItem item = new MenuItem(
            AppUtils.generateId("ITEM"), restaurantId,
            name, description, category, price
        );
        RepositoryFactory.menuItems().save(item.getId(), item);
        return item;
    }

    public MenuItemAddon addAddon(String menuItemId, String name, double extraPrice) {
        MenuItem item = RepositoryFactory.menuItems().findById(menuItemId)
            .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + menuItemId));
        MenuItemAddon addon = new MenuItemAddon(AppUtils.generateId("ADD"), name, extraPrice);
        item.addAddon(addon);
        RepositoryFactory.menuItems().save(menuItemId, item);
        return addon;
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public List<MenuItem> getMenuForRestaurant(String restaurantId) {
        return RepositoryFactory.menuItems().findByRestaurant(restaurantId);
    }

    public List<MenuItem> getAvailableMenu(String restaurantId) {
        return RepositoryFactory.menuItems().findByRestaurant(restaurantId).stream()
            .filter(MenuItem::isOrderable)
            .collect(Collectors.toList());
    }

    /** Group available items by category. */
    public Map<String, List<MenuItem>> getMenuByCategory(String restaurantId) {
        return getAvailableMenu(restaurantId).stream()
            .collect(Collectors.groupingBy(
                MenuItem::getCategory,
                TreeMap::new,
                Collectors.toList()
            ));
    }

    /** Search menu items by name (case-insensitive). */
    public List<MenuItem> searchMenu(String restaurantId, String query) {
        String q = query.toLowerCase();
        return getAvailableMenu(restaurantId).stream()
            .filter(m -> m.getName().toLowerCase().contains(q)
                      || (m.getDescription() != null && m.getDescription().toLowerCase().contains(q)))
            .collect(Collectors.toList());
    }

    public Optional<MenuItem> findById(String id) {
        return RepositoryFactory.menuItems().findById(id);
    }

    // ── Management ───────────────────────────────────────────────────────────

    public void setAvailability(String menuItemId, boolean available) {
        RepositoryFactory.menuItems().findById(menuItemId).ifPresent(item -> {
            item.setAvailable(available);
            RepositoryFactory.menuItems().save(menuItemId, item);
        });
    }

    public void updateQuantity(String menuItemId, int quantity) {
        RepositoryFactory.menuItems().findById(menuItemId).ifPresent(item -> {
            item.setQuantity(quantity);
            RepositoryFactory.menuItems().save(menuItemId, item);
        });
    }

    public void updateItem(MenuItem item) {
        RepositoryFactory.menuItems().save(item.getId(), item);
    }

    public void deleteItem(String menuItemId) {
        RepositoryFactory.menuItems().delete(menuItemId);
    }

    /** Decrement stock after an order is placed. */
    public void decrementStock(String menuItemId, int quantity) {
        RepositoryFactory.menuItems().findById(menuItemId).ifPresent(item -> {
            item.decrementQuantity(quantity);
            RepositoryFactory.menuItems().save(menuItemId, item);
        });
    }
}
