package com.fooddelivery.ui.restaurant.dashboard.request;

public class RestaurantRegistrationForm {

    private final String name;
    private final String cuisineType;
    private final String street;
    private final String area;
    private final String phone;
    private final String minOrderText;
    private final String etaText;

    public RestaurantRegistrationForm(String name,
                                      String cuisineType,
                                      String street,
                                      String area,
                                      String phone,
                                      String minOrderText,
                                      String etaText) {
        this.name = name;
        this.cuisineType = cuisineType;
        this.street = street;
        this.area = area;
        this.phone = phone;
        this.minOrderText = minOrderText;
        this.etaText = etaText;
    }

    public String getName() {
        return name;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public String getStreet() {
        return street;
    }

    public String getArea() {
        return area;
    }

    public String getPhone() {
        return phone;
    }

    public String getMinOrderText() {
        return minOrderText;
    }

    public String getEtaText() {
        return etaText;
    }
}