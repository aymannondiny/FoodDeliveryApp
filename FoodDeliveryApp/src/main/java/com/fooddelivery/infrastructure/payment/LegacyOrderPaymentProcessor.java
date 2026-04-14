package com.fooddelivery.infrastructure.payment;

import com.fooddelivery.domain.service.OrderPaymentProcessor;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;
import com.fooddelivery.service.PaymentService;

public class LegacyOrderPaymentProcessor implements OrderPaymentProcessor {

    @Override
    public Payment process(Order order) {
        return PaymentService.getInstance().processPayment(order);
    }

    @Override
    public void markCashOnDeliveryCollected(String orderId) {
        PaymentService.getInstance().markCodCollected(orderId);
    }
}