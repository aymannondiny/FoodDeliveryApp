package com.fooddelivery.domain.service;

import com.fooddelivery.model.Order;

public interface RiderAssigner {
    void assignTo(Order order);
}