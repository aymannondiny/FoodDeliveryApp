package com.fooddelivery.api.restaurant;

public class RestaurantSearchCriteria {

    private final String area;
    private final String cuisine;
    private final String search;

    public RestaurantSearchCriteria(String area, String cuisine, String search) {
        this.area = area;
        this.cuisine = cuisine;
        this.search = search;
    }

    public String getArea() {
        return area;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getSearch() {
        return search;
    }

    public boolean hasArea() {
        return area != null && !area.isBlank();
    }

    public boolean hasCuisine() {
        return cuisine != null && !cuisine.isBlank();
    }

    public boolean hasSearch() {
        return search != null && !search.isBlank();
    }
}