package com.fooddelivery.infrastructure.order;

import com.fooddelivery.domain.service.DeliveryFeeCalculator;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;

public class DefaultDeliveryFeeCalculator implements DeliveryFeeCalculator {

    @Override
    public double calculate(Restaurant restaurant, Address deliveryAddress) {
        if (restaurant == null || deliveryAddress == null || restaurant.getAddress() == null) {
            return 30.0;
        }

        double distance = restaurant.getAddress().distanceTo(deliveryAddress);
        double fee = Math.max(30.0, restaurant.getDeliveryFeePerKm() * Math.max(1.0, distance));

        if (Double.isNaN(fee) || Double.isInfinite(fee) || fee > 150.0) {
            fee = 150.0;
        }

        return fee;
    }
}