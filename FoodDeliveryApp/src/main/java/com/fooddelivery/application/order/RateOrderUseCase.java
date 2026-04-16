package com.fooddelivery.application.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.RestaurantRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class RateOrderUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RiderRepository riderRepository;

    public RateOrderUseCase(OrderRepository orderRepository,
                            RestaurantRepository restaurantRepository,
                            RiderRepository riderRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.riderRepository = riderRepository;
    }

    public Order execute(String orderId, double foodRating, double riderRating) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Only delivered orders can be rated.");
        }

        if (order.isRated()) {
            throw new IllegalStateException("This order has already been rated.");
        }

        validateRating(foodRating, "Food rating");
        validateRating(riderRating, "Rider rating");

        order.setRestaurantRating(foodRating);
        order.setRiderRating(riderRating);
        order.setRated(true);
        orderRepository.save(orderId, order);

        restaurantRepository.findById(order.getRestaurantId()).ifPresent(restaurant -> {
            restaurant.addRating(foodRating);
            restaurantRepository.save(restaurant.getId(), restaurant);
        });

        if (order.getRiderId() != null) {
            riderRepository.findById(order.getRiderId()).ifPresent(rider -> {
                rider.addRating(riderRating);
                riderRepository.save(rider.getId(), rider);
            });
        }

        return order;
    }

    private void validateRating(double rating, String fieldName) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 5.");
        }
    }
}