package com.fooddelivery.application.payment;

import com.fooddelivery.domain.repository.PaymentRepository;
import com.fooddelivery.model.Payment;

import java.util.Optional;

public class GetPaymentForOrderUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentForOrderUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Optional<Payment> execute(String orderId) {
        return paymentRepository.findByOrder(orderId);
    }
}