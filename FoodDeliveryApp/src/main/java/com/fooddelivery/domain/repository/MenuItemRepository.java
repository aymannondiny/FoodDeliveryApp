package com.fooddelivery.domain.repository;

import com.fooddelivery.model.MenuItem;

import java.util.List;

public interface MenuItemRepository extends DataRepository<MenuItem> {
    List<MenuItem> findByRestaurant(String restaurantId);
}