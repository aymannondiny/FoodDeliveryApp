package com.fooddelivery.ui.customer.cart.viewmodel;

public class CartItemViewModel {

    private final int index;
    private final String name;
    private final String unitPriceText;
    private final int quantity;
    private final String lineTotalText;
    private final int availableStock;

    public CartItemViewModel(int index,
                             String name,
                             String unitPriceText,
                             int quantity,
                             String lineTotalText,
                             int availableStock) {
        this.index = index;
        this.name = name;
        this.unitPriceText = unitPriceText;
        this.quantity = quantity;
        this.lineTotalText = lineTotalText;
        this.availableStock = availableStock;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getUnitPriceText() {
        return unitPriceText;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLineTotalText() {
        return lineTotalText;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public boolean isUnlimitedStock() {
        return availableStock == -1;
    }

    public boolean isAtMaxStock() {
        return !isUnlimitedStock() && quantity >= availableStock;
    }
}