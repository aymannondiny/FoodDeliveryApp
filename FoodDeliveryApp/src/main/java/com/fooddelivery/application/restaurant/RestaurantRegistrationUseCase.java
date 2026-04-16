package com.fooddelivery.application.restaurant;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.domain.repository.RestaurantRepository;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Restaurant;

public class RestaurantRegistrationUseCase {

    private final RestaurantRepository restaurantRepository;
    private final IdGenerator idGenerator;

    public RestaurantRegistrationUseCase(RestaurantRepository restaurantRepository,
                                         IdGenerator idGenerator) {
        this.restaurantRepository = restaurantRepository;
        this.idGenerator = idGenerator;
    }

    public Restaurant execute(String ownerId,
                              String name,
                              String cuisineType,
                              Address address,
                              String phone,
                              double deliveryFeePerKm,
                              double minOrderAmount,
                              int estimatedDeliveryMinutes) {
        Restaurant restaurant = new Restaurant(
                idGenerator.nextId("RST"),
                ownerId,
                name,
                cuisineType,
                address
        );

        restaurant.setPhoneNumber(phone);
        restaurant.setDeliveryFeePerKm(deliveryFeePerKm);
        restaurant.setMinOrderAmount(minOrderAmount);
        restaurant.setEstimatedDeliveryMinutes(estimatedDeliveryMinutes);
        restaurant.setApproved(true);
        restaurant.setOpen(true);

        restaurantRepository.save(restaurant.getId(), restaurant);
        return restaurant;
    }
}