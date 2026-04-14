package com.fooddelivery.api.restaurant;

import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.model.Restaurant;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestaurantResponseMapper implements Mapper<Restaurant, Map<String, Object>> {

    @Override
    public Map<String, Object> map(Restaurant restaurant) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", restaurant.getId());
        m.put("name", restaurant.getName());
        m.put("cuisineType", restaurant.getCuisineType());
        m.put("description", restaurant.getDescription());
        m.put("rating", restaurant.getRating());
        m.put("totalRatings", restaurant.getTotalRatings());
        m.put("isOpen", restaurant.isCurrentlyOpen());
        m.put("minOrderAmount", restaurant.getMinOrderAmount());
        m.put("deliveryFeePerKm", restaurant.getDeliveryFeePerKm());
        m.put("estimatedDeliveryMin", restaurant.getEstimatedDeliveryMinutes());
        m.put("phone", restaurant.getPhoneNumber());

        if (restaurant.getAddress() != null) {
            Map<String, Object> address = new LinkedHashMap<>();
            address.put("street", restaurant.getAddress().getStreet());
            address.put("area", restaurant.getAddress().getArea());
            address.put("city", restaurant.getAddress().getCity());
            address.put("lat", restaurant.getAddress().getLatitude());
            address.put("lng", restaurant.getAddress().getLongitude());
            m.put("address", address);
        }

        return m;
    }
}