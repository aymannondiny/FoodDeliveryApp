package com.fooddelivery.repository;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class OrderRepository extends FileRepository<Order> {
    private static OrderRepository instance;

    private OrderRepository() {
        super("data/orders.json", new TypeToken<Map<String, Order>>(){}.getType());
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    public List<Order> findByCustomer(String customerId) {
        return findWhere(o -> customerId.equals(o.getCustomerId()));
    }

    public List<Order> findByRestaurant(String restaurantId) {
        return findWhere(o -> restaurantId.equals(o.getRestaurantId()));
    }

    public List<Order> findActiveByRestaurant(String restaurantId) {
        return findWhere(o -> restaurantId.equals(o.getRestaurantId())
                           && o.getStatus() != OrderStatus.DELIVERED
                           && o.getStatus() != OrderStatus.CANCELLED);
    }
}
