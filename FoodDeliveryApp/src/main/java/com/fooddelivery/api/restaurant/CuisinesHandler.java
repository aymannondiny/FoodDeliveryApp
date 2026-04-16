package com.fooddelivery.api.restaurant;

import com.fooddelivery.api.common.BaseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class CuisinesHandler extends BaseHandler {

    private final RestaurantReader restaurantReader;

    public CuisinesHandler(RestaurantReader restaurantReader) {
        this.restaurantReader = restaurantReader;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange, Map<String, String> params) {
        return restaurantReader.getAllCuisineTypes();
    }
}