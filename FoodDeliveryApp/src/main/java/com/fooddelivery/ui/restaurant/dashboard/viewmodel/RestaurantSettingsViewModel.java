package com.fooddelivery.ui.restaurant.dashboard.viewmodel;

public class RestaurantSettingsViewModel {

    private final String name;
    private final String phone;
    private final String minOrderText;
    private final String etaText;
    private final String openTime;
    private final String closeTime;
    private final String description;
    private final String ratingText;
    private final int totalRatings;

    public RestaurantSettingsViewModel(String name,
                                       String phone,
                                       String minOrderText,
                                       String etaText,
                                       String openTime,
                                       String closeTime,
                                       String description,
                                       String ratingText,
                                       int totalRatings) {
        this.name = name;
        this.phone = phone;
        this.minOrderText = minOrderText;
        this.etaText = etaText;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.description = description;
        this.ratingText = ratingText;
        this.totalRatings = totalRatings;
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

    public String getRatingText() {
        return ratingText;
    }

    public int getTotalRatings() {
        return totalRatings;
    }
}