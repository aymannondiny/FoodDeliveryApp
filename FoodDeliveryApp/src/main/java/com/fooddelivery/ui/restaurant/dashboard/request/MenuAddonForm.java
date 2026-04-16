package com.fooddelivery.ui.restaurant.dashboard.request;

public class MenuAddonForm {

    private final String name;
    private final String extraPriceText;

    public MenuAddonForm(String name, String extraPriceText) {
        this.name = name;
        this.extraPriceText = extraPriceText;
    }

    public String getName() {
        return name;
    }

    public String getExtraPriceText() {
        return extraPriceText;
    }
}