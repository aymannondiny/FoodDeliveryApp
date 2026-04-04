package com.fooddelivery.repository;

import com.fooddelivery.model.Restaurant;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class RestaurantRepository extends FileRepository<Restaurant> {
    private static RestaurantRepository instance;

    private RestaurantRepository() {
        super("data/restaurants.json", new TypeToken<Map<String, Restaurant>>(){}.getType());
    }

    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) instance = new RestaurantRepository();
        return instance;
    }

    public List<Restaurant> findByArea(String area) {
        return findWhere(r -> r.getAddress() != null
                           && r.getAddress().getArea().equalsIgnoreCase(area));
    }

    public List<Restaurant> findByOwner(String ownerId) {
        return findWhere(r -> ownerId.equals(r.getOwnerId()));
    }
}
