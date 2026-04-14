package com.fooddelivery.domain.service;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;

public interface DeliveryFeeCalculator {
    double calculate(Restaurant restaurant, Address deliveryAddress);
}