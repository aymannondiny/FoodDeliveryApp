package com.fooddelivery.domain.service;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;

public interface OrderPaymentProcessor {

    Payment process(Order order);

    void markCashOnDeliveryCollected(String orderId);
}