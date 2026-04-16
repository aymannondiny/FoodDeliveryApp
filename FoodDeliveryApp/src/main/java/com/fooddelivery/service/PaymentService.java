package com.fooddelivery.service;

import com.fooddelivery.application.payment.GetPaymentForOrderUseCase;
import com.fooddelivery.application.payment.MarkCashOnDeliveryCollectedUseCase;
import com.fooddelivery.application.payment.ProcessPaymentUseCase;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;

import java.util.Optional;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer payment use cases from AppContext.
 */
@Deprecated
public class PaymentService {

    private static PaymentService instance;

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final MarkCashOnDeliveryCollectedUseCase markCashOnDeliveryCollectedUseCase;
    private final GetPaymentForOrderUseCase getPaymentForOrderUseCase;

    private PaymentService() {
        AppContext context = AppContext.create();
        this.processPaymentUseCase = context.processPaymentUseCase();
        this.markCashOnDeliveryCollectedUseCase = context.markCashOnDeliveryCollectedUseCase();
        this.getPaymentForOrderUseCase = context.getPaymentForOrderUseCase();
    }

    public static synchronized PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    public Payment processPayment(Order order) {
        return processPaymentUseCase.execute(order);
    }

    public void markCodCollected(String orderId) {
        markCashOnDeliveryCollectedUseCase.execute(orderId);
    }

    public Optional<Payment> getPaymentForOrder(String orderId) {
        return getPaymentForOrderUseCase.execute(orderId);
    }
}