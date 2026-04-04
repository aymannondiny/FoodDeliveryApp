package com.fooddelivery.service;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.util.Optional;

/**
 * Handles payment processing for orders.
 * For non-COD methods, simulates a gateway call and records the result.
 */
public class PaymentService {

    private static PaymentService instance;

    private PaymentService() {}

    public static synchronized PaymentService getInstance() {
        if (instance == null) instance = new PaymentService();
        return instance;
    }

    /**
     * Process payment for an order.
     * For CASH_ON_DELIVERY, immediately marks as COMPLETED.
     * For digital methods, simulates gateway (90 % success rate in demo).
     */
    public Payment processPayment(Order order) {
        Payment payment = new Payment(
            AppUtils.generateId("PAY"),
            order.getId(),
            order.getPaymentMethod(),
            order.getTotalAmount()
        );

        if (order.getPaymentMethod() == Order.PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(Payment.Status.PENDING); // Collected on delivery
            payment.setTransactionId("COD-" + order.getId());
        } else {
            // Simulate gateway call
            boolean success = simulateGateway(order.getPaymentMethod(), order.getTotalAmount());
            if (success) {
                payment.setStatus(Payment.Status.COMPLETED);
                payment.setTransactionId("TXN-" + AppUtils.generateId(""));
            } else {
                payment.setStatus(Payment.Status.FAILED);
                payment.setFailureReason("Gateway timeout – please retry.");
            }
        }

        RepositoryFactory.payments().save(payment.getId(), payment);
        return payment;
    }

    public void markCodCollected(String orderId) {
        RepositoryFactory.payments().findByOrder(orderId).ifPresent(p -> {
            p.setStatus(Payment.Status.COMPLETED);
            RepositoryFactory.payments().save(p.getId(), p);
        });
    }

    public Optional<Payment> getPaymentForOrder(String orderId) {
        return RepositoryFactory.payments().findByOrder(orderId);
    }

    /** Demo-only: 95 % success rate for digital payments. */
    private boolean simulateGateway(Order.PaymentMethod method, double amount) {
        return Math.random() > 0.05;
    }
}
