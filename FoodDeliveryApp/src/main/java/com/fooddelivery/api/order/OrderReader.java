package com.fooddelivery.api.order;

import com.fooddelivery.model.Order;

import java.util.Optional;

public interface OrderReader {
    Optional<Order> findById(String orderId);
}