package com.fooddelivery.ui.restaurant.dashboard.request;

public class MenuItemForm {

    private final String name;
    private final String category;
    private final String priceText;
    private final String description;
    private final String stockText;

    public MenuItemForm(String name,
                        String category,
                        String priceText,
                        String description,
                        String stockText) {
        this.name = name;
        this.category = category;
        this.priceText = priceText;
        this.description = description;
        this.stockText = stockText;
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

    public String getDescription() {
        return description;
    }

    public String getStockText() {
        return stockText;
    }
}