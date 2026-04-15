package com.fooddelivery.ui.restaurant.dashboard.viewmodel;

public class RestaurantMenuRowViewModel {

    private final String id;
    private final String name;
    private final String category;
    private final String priceText;
    private final boolean available;
    private final String stockText;

    public RestaurantMenuRowViewModel(String id,
                                      String name,
                                      String category,
                                      String priceText,
                                      boolean available,
                                      String stockText) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.priceText = priceText;
        this.available = available;
        this.stockText = stockText;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPriceText() {
        return priceText;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getStockText() {
        return stockText;
    }
}