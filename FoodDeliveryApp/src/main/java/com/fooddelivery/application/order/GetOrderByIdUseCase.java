package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;

import java.util.Optional;

public class GetOrderByIdUseCase {

    private final OrderRepository orderRepository;

    public GetOrderByIdUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> execute(String orderId) {
        return orderRepository.findById(orderId);
    }
}