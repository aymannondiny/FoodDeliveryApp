package com.fooddelivery.application.order;

import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.domain.service.OrderPaymentProcessor;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class CompleteDeliveryUseCase {

    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final OrderStatusPolicy orderStatusPolicy;
    private final OrderPaymentProcessor orderPaymentProcessor;

    public CompleteDeliveryUseCase(OrderRepository orderRepository,
                                   RiderRepository riderRepository,
                                   OrderStatusPolicy orderStatusPolicy,
                                   OrderPaymentProcessor orderPaymentProcessor) {
        this.orderRepository = orderRepository;
        this.riderRepository = riderRepository;
        this.orderStatusPolicy = orderStatusPolicy;
        this.orderPaymentProcessor = orderPaymentProcessor;
    }

    public Order execute(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        orderStatusPolicy.ensureTransitionAllowed(order, OrderStatus.DELIVERED);
        order.advanceStatus(OrderStatus.DELIVERED);
        orderRepository.save(orderId, order);

        if (order.getRiderId() != null) {
            riderRepository.findById(order.getRiderId()).ifPresent(rider -> {
                rider.setCurrentOrderId(null);
                rider.setAvailable(true);
                rider.setTotalDeliveries(rider.getTotalDeliveries() + 1);
                riderRepository.save(rider.getId(), rider);
            });
        }

        orderPaymentProcessor.markCashOnDeliveryCollected(orderId);
        return order;
    }
}