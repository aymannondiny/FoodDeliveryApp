package com.fooddelivery.domain.policy;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public interface OrderStatusPolicy {
    void ensureTransitionAllowed(Order order, OrderStatus newStatus);
}