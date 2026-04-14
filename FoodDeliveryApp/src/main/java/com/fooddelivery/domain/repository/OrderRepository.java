package com.fooddelivery.domain.repository;

import com.fooddelivery.model.Order;

import java.util.List;

public interface OrderRepository extends DataRepository<Order> {
    List<Order> findByCustomer(String customerId);
    List<Order> findByRestaurant(String restaurantId);
    List<Order> findActiveByRestaurant(String restaurantId);
}