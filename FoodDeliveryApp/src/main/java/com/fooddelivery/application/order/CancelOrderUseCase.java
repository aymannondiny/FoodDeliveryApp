package com.fooddelivery.application.order;

import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderStatusPolicy orderStatusPolicy;
    private final RiderRepository riderRepository;

    public CancelOrderUseCase(OrderRepository orderRepository,
                              OrderStatusPolicy orderStatusPolicy,
                              RiderRepository riderRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusPolicy = orderStatusPolicy;
        this.riderRepository = riderRepository;
    }

    public Order execute(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        orderStatusPolicy.ensureTransitionAllowed(order, OrderStatus.CANCELLED);
        order.advanceStatus(OrderStatus.CANCELLED);
        orderRepository.save(orderId, order);

        freeAssignedRider(order);

        return order;
    }

    private void freeAssignedRider(Order order) {
        if (order.getRiderId() == null) {
            return;
        }

        riderRepository.findById(order.getRiderId()).ifPresent(rider -> {
            rider.setCurrentOrderId(null);
            rider.setAvailable(true);
            riderRepository.save(rider.getId(), rider);
        });
    }
}