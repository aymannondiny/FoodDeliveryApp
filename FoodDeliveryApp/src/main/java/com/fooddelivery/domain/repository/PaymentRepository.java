package com.fooddelivery.domain.repository;

import com.fooddelivery.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends DataRepository<Payment> {
    Optional<Payment> findByOrder(String orderId);
}