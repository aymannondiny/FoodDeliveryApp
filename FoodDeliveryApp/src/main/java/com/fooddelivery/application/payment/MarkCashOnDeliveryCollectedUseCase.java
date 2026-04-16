package com.fooddelivery.application.payment;

import com.fooddelivery.domain.repository.PaymentRepository;

public class MarkCashOnDeliveryCollectedUseCase {

    private final PaymentRepository paymentRepository;

    public MarkCashOnDeliveryCollectedUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void execute(String orderId) {
        paymentRepository.findByOrder(orderId).ifPresent(payment -> {
            payment.setStatus(com.fooddelivery.model.Payment.Status.COMPLETED);
            paymentRepository.save(payment.getId(), payment);
        });
    }
}