package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;

import java.util.Comparator;
import java.util.List;

public class GetActiveRestaurantOrdersUseCase {

    private final OrderRepository orderRepository;

    public GetActiveRestaurantOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute(String restaurantId) {
        return orderRepository.findActiveByRestaurant(restaurantId).stream()
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .toList();
    }
}