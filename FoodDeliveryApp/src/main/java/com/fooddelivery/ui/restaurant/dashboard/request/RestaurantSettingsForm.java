package com.fooddelivery.ui.restaurant.dashboard.request;

public class RestaurantSettingsForm {

    private final String name;
    private final String phone;
    private final String minOrderText;
    private final String etaText;
    private final String openTime;
    private final String closeTime;
    private final String description;

    public RestaurantSettingsForm(String name,
                                  String phone,
                                  String minOrderText,
                                  String etaText,
                                  String openTime,
                                  String closeTime,
                                  String description) {
        this.name = name;
        this.phone = phone;
        this.minOrderText = minOrderText;
        this.etaText = etaText;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.description = description;
    }

    public String getName() {
        return name;
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

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public String getDescription() {
        return description;
    }
}