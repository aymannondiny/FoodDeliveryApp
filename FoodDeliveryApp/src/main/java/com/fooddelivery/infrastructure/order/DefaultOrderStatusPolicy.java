package com.fooddelivery.infrastructure.order;

import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class DefaultOrderStatusPolicy implements OrderStatusPolicy {

    @Override
    public void ensureTransitionAllowed(Order order, OrderStatus newStatus) {
        if (order == null) {
            throw new IllegalArgumentException("Order is required.");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required.");
        }

        OrderStatus current = order.getStatus();

        if (current == newStatus) {
            return;
        }

        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Order cannot change status after " + current + ".");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            if (!order.isCancellable()) {
                throw new IllegalStateException("Order cannot be cancelled at this stage.");
            }
            return;
        }

        int expectedNextOrdinal = current.ordinal() + 1;
        if (newStatus.ordinal() != expectedNextOrdinal) {
            throw new IllegalStateException(
                    "Invalid status transition from " + current + " to " + newStatus + "."
            );
        }
    }
}