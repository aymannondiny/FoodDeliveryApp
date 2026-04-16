package com.fooddelivery.application.order;

import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.service.RiderAssigner;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class AdvanceOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final OrderStatusPolicy orderStatusPolicy;
    private final RiderAssigner riderAssigner;

    public AdvanceOrderStatusUseCase(OrderRepository orderRepository,
                                     OrderStatusPolicy orderStatusPolicy,
                                     RiderAssigner riderAssigner) {
        this.orderRepository = orderRepository;
        this.orderStatusPolicy = orderStatusPolicy;
        this.riderAssigner = riderAssigner;
    }

    public Order execute(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        orderStatusPolicy.ensureTransitionAllowed(order, newStatus);
        order.advanceStatus(newStatus);
        orderRepository.save(orderId, order);

        if (newStatus == OrderStatus.READY && order.getRiderId() == null) {
            riderAssigner.assignTo(order);
        }

        return order;
    }
}