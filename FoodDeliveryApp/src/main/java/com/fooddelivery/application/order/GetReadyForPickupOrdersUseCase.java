package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetReadyForPickupOrdersUseCase {

    private final OrderRepository orderRepository;

    public GetReadyForPickupOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.READY)
                .filter(order -> order.getRiderId() == null)
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .collect(Collectors.toList());
    }
}