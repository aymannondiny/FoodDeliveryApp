package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetDeliveredOrdersForRiderUseCase {

    private final OrderRepository orderRepository;

    public GetDeliveredOrdersForRiderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute(String riderId) {
        return orderRepository.findAll().stream()
                .filter(order -> riderId.equals(order.getRiderId()))
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}