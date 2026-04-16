package com.fooddelivery.application.order;

import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.Rider;

public class AcceptPickupUseCase {

    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final OrderStatusPolicy orderStatusPolicy;

    public AcceptPickupUseCase(OrderRepository orderRepository,
                               RiderRepository riderRepository,
                               OrderStatusPolicy orderStatusPolicy) {
        this.orderRepository = orderRepository;
        this.riderRepository = riderRepository;
        this.orderStatusPolicy = orderStatusPolicy;
    }

    public Order execute(String riderId, String orderId) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found: " + riderId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!rider.isAvailable()) {
            throw new IllegalStateException("Rider is currently unavailable.");
        }

        if (rider.getCurrentOrderId() != null) {
            throw new IllegalStateException("Rider already has an active delivery.");
        }

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Only READY orders can be picked up.");
        }

        if (order.getRiderId() != null) {
            throw new IllegalStateException("This order has already been assigned to another rider.");
        }

        orderStatusPolicy.ensureTransitionAllowed(order, OrderStatus.PICKED_UP);

        rider.setCurrentOrderId(order.getId());
        rider.setAvailable(false);
        riderRepository.save(rider.getId(), rider);

        order.setRiderId(rider.getId());
        order.advanceStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order.getId(), order);

        return order;
    }
}