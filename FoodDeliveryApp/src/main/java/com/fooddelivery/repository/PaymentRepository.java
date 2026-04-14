package com.fooddelivery.repository;

import com.fooddelivery.model.Payment;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Optional;

public class PaymentRepository extends FileRepository<Payment>
        implements com.fooddelivery.domain.repository.PaymentRepository {

    private static PaymentRepository instance;

    private PaymentRepository() {
        super("data/payments.json", new TypeToken<Map<String, Payment>>() {}.getType());
    }

    public static synchronized PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }

    @Override
    public Optional<Payment> findByOrder(String orderId) {
        return findWhere(payment -> orderId.equals(payment.getOrderId()))
                .stream()
                .findFirst();
    }
}