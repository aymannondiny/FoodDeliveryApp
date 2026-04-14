package com.fooddelivery.api.order;

import com.fooddelivery.model.Order;
import com.fooddelivery.service.OrderService;

import java.util.Optional;

public class DefaultOrderReader implements OrderReader {

    private final OrderService orderService;

    public DefaultOrderReader(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return orderService.findById(orderId);
    }
}