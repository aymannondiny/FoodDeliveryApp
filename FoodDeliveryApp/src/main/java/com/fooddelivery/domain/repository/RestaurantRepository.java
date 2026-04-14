package com.fooddelivery.domain.repository;

import com.fooddelivery.model.Restaurant;

import java.util.List;

public interface RestaurantRepository extends DataRepository<Restaurant> {
    List<Restaurant> findByArea(String area);
    List<Restaurant> findByOwner(String ownerId);
}