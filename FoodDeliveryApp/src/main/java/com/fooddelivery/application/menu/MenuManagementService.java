package com.fooddelivery.application.menu;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.domain.repository.MenuItemRepository;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;

public class MenuManagementService {

    private final MenuItemRepository menuItemRepository;
    private final IdGenerator idGenerator;

    public MenuManagementService(MenuItemRepository menuItemRepository,
                                 IdGenerator idGenerator) {
        this.menuItemRepository = menuItemRepository;
        this.idGenerator = idGenerator;
    }

    public MenuItem addMenuItem(String restaurantId,
                                String name,
                                String description,
                                String category,
                                double price) {
        MenuItem item = new MenuItem(
                idGenerator.nextId("ITEM"),
                restaurantId,
                name,
                description,
                category,
                price
        );

        menuItemRepository.save(item.getId(), item);
        return item;
    }

    public MenuItemAddon addAddon(String menuItemId, String name, double extraPrice) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + menuItemId));

        MenuItemAddon addon = new MenuItemAddon(
                idGenerator.nextId("ADD"),
                name,
                extraPrice
        );

        item.addAddon(addon);
        menuItemRepository.save(menuItemId, item);
        return addon;
    }

    public void setAvailability(String menuItemId, boolean available) {
        menuItemRepository.findById(menuItemId).ifPresent(item -> {
            item.setAvailable(available);
            menuItemRepository.save(menuItemId, item);
        });
    }

    public void updateQuantity(String menuItemId, int quantity) {
        menuItemRepository.findById(menuItemId).ifPresent(item -> {
            item.setQuantity(quantity);
            menuItemRepository.save(menuItemId, item);
        });
    }

    public void updateItem(MenuItem item) {
        menuItemRepository.save(item.getId(), item);
    }

    public void deleteItem(String menuItemId) {
        menuItemRepository.delete(menuItemId);
    }

    public void decrementStock(String menuItemId, int quantity) {
        menuItemRepository.findById(menuItemId).ifPresent(item -> {
            item.decrementQuantity(quantity);
            menuItemRepository.save(menuItemId, item);
        });
    }
}