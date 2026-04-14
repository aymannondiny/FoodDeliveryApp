package com.fooddelivery.repository;

import com.fooddelivery.model.Restaurant;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class RestaurantRepository extends FileRepository<Restaurant>
        implements com.fooddelivery.domain.repository.RestaurantRepository {

    private static RestaurantRepository instance;

    private RestaurantRepository() {
        super("data/restaurants.json", new TypeToken<Map<String, Restaurant>>() {}.getType());
    }

    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    @Override
    public List<Restaurant> findByArea(String area) {
        return findWhere(restaurant ->
                restaurant.getAddress() != null
                        && restaurant.getAddress().getArea() != null
                        && restaurant.getAddress().getArea().equalsIgnoreCase(area));
    }

    @Override
    public List<Restaurant> findByOwner(String ownerId) {
        return findWhere(restaurant -> ownerId.equals(restaurant.getOwnerId()));
    }
}