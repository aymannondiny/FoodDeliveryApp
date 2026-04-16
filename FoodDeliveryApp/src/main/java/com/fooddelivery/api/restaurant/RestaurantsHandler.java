package com.fooddelivery.api.restaurant;

import com.fooddelivery.api.common.BaseHandler;
import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.model.Restaurant;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantsHandler extends BaseHandler {

    private final RestaurantReader restaurantReader;
    private final Mapper<Restaurant, Map<String, Object>> restaurantMapper;

    public RestaurantsHandler(RestaurantReader restaurantReader,
                              Mapper<Restaurant, Map<String, Object>> restaurantMapper) {
        this.restaurantReader = restaurantReader;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange, Map<String, String> params) {
        RestaurantSearchCriteria criteria = new RestaurantSearchCriteria(
                params.get("area"),
                params.get("cuisine"),
                params.get("search")
        );

        List<Map<String, Object>> payload = restaurantReader.findByCriteria(criteria)
                .stream()
                .map(restaurantMapper::map)
                .collect(Collectors.toList());

        return payload;
    }
}