package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;

import java.util.Comparator;
import java.util.List;

public class GetRestaurantOrdersUseCase {

    private final OrderRepository orderRepository;

    public GetRestaurantOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute(String restaurantId) {
        return orderRepository.findByRestaurant(restaurantId).stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
    }
}