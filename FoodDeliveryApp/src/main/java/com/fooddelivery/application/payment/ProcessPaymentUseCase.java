package com.fooddelivery.application.payment;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.domain.repository.PaymentRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;

public class ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final IdGenerator idGenerator;

    public ProcessPaymentUseCase(PaymentRepository paymentRepository,
                                 IdGenerator idGenerator) {
        this.paymentRepository = paymentRepository;
        this.idGenerator = idGenerator;
    }

    public Payment execute(Order order) {
        Payment payment = new Payment(
                idGenerator.nextId("PAY"),
                order.getId(),
                order.getPaymentMethod(),
                order.getTotalAmount()
        );

        if (order.getPaymentMethod() == Order.PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(Payment.Status.PENDING);
            payment.setTransactionId("COD-" + order.getId());
        } else {
            boolean success = simulateGateway(order.getPaymentMethod(), order.getTotalAmount());
            if (success) {
                payment.setStatus(Payment.Status.COMPLETED);
                payment.setTransactionId(idGenerator.nextId("TXN"));
            } else {
                payment.setStatus(Payment.Status.FAILED);
                payment.setFailureReason("Gateway timeout – please retry.");
            }
        }

        paymentRepository.save(payment.getId(), payment);
        return payment;
    }

    private boolean simulateGateway(Order.PaymentMethod method, double amount) {
        return Math.random() > 0.05;
    }
}