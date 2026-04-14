package com.fooddelivery.application.order;

import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class AdvanceOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final OrderStatusPolicy orderStatusPolicy;

    public AdvanceOrderStatusUseCase(OrderRepository orderRepository,
                                     OrderStatusPolicy orderStatusPolicy) {
        this.orderRepository = orderRepository;
        this.orderStatusPolicy = orderStatusPolicy;
    }

    public Order execute(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        orderStatusPolicy.ensureTransitionAllowed(order, newStatus);
        order.advanceStatus(newStatus);
        orderRepository.save(orderId, order);
        return order;
    }
}