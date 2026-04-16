package com.fooddelivery.application.menu;

import com.fooddelivery.domain.repository.MenuItemRepository;
import com.fooddelivery.model.MenuItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MenuQueryService {

    private final MenuItemRepository menuItemRepository;

    public MenuQueryService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getMenuForRestaurant(String restaurantId) {
        return menuItemRepository.findByRestaurant(restaurantId);
    }

    public List<MenuItem> getAvailableMenu(String restaurantId) {
        return menuItemRepository.findByRestaurant(restaurantId).stream()
                .filter(MenuItem::isOrderable)
                .collect(Collectors.toList());
    }

    public Map<String, List<MenuItem>> getMenuByCategory(String restaurantId) {
        return getAvailableMenu(restaurantId).stream()
                .collect(Collectors.groupingBy(
                        MenuItem::getCategory,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    public List<MenuItem> searchMenu(String restaurantId, String query) {
        String q = query.toLowerCase();

        return getAvailableMenu(restaurantId).stream()
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(q)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public Optional<MenuItem> findById(String id) {
        return menuItemRepository.findById(id);
    }
}