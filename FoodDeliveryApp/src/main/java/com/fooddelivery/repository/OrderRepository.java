package com.fooddelivery.repository;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class OrderRepository extends FileRepository<Order>
        implements com.fooddelivery.domain.repository.OrderRepository {

    private static OrderRepository instance;

    private OrderRepository() {
        super("data/orders.json", new TypeToken<Map<String, Order>>() {}.getType());
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    @Override
    public List<Order> findByCustomer(String customerId) {
        return findWhere(order -> customerId.equals(order.getCustomerId()));
    }

    @Override
    public List<Order> findByRestaurant(String restaurantId) {
        return findWhere(order -> restaurantId.equals(order.getRestaurantId()));
    }

    @Override
    public List<Order> findActiveByRestaurant(String restaurantId) {
        return findWhere(order ->
                restaurantId.equals(order.getRestaurantId())
                        && order.getStatus() != OrderStatus.DELIVERED
                        && order.getStatus() != OrderStatus.CANCELLED);
    }
}