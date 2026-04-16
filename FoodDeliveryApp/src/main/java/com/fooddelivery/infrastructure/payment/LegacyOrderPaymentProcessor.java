package com.fooddelivery.infrastructure.payment;

import com.fooddelivery.application.payment.MarkCashOnDeliveryCollectedUseCase;
import com.fooddelivery.application.payment.ProcessPaymentUseCase;
import com.fooddelivery.domain.service.OrderPaymentProcessor;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;

/**
 * Transitional adapter from domain payment processing abstraction
 * to application-layer payment use cases.
 */
public class LegacyOrderPaymentProcessor implements OrderPaymentProcessor {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final MarkCashOnDeliveryCollectedUseCase markCashOnDeliveryCollectedUseCase;

    public LegacyOrderPaymentProcessor(ProcessPaymentUseCase processPaymentUseCase,
                                       MarkCashOnDeliveryCollectedUseCase markCashOnDeliveryCollectedUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.markCashOnDeliveryCollectedUseCase = markCashOnDeliveryCollectedUseCase;
    }

    @Override
    public Payment process(Order order) {
        return processPaymentUseCase.execute(order);
    }

    @Override
    public void markCashOnDeliveryCollected(String orderId) {
        markCashOnDeliveryCollectedUseCase.execute(orderId);
    }
}