package com.fooddelivery.api.order;

import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.model.Order;

import java.util.Optional;

public class DefaultOrderReader implements OrderReader {

    private final GetOrderByIdUseCase getOrderByIdUseCase;

    public DefaultOrderReader(GetOrderByIdUseCase getOrderByIdUseCase) {
        this.getOrderByIdUseCase = getOrderByIdUseCase;
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return getOrderByIdUseCase.execute(orderId);
    }
}