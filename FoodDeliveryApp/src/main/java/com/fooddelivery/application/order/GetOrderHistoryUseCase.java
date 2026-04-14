package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;

import java.util.Comparator;
import java.util.List;

public class GetOrderHistoryUseCase {

    private final OrderRepository orderRepository;

    public GetOrderHistoryUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute(String customerId) {
        return orderRepository.findByCustomer(customerId).stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
    }
}