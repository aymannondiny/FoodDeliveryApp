package com.fooddelivery.api.coupon;

public class CouponValidationResult {

    private final String code;
    private final double discountPercent;
    private final double subtotal;
    private final double discountAmount;
    private final double finalAmount;

    public CouponValidationResult(String code,
                                  double discountPercent,
                                  double subtotal,
                                  double discountAmount,
                                  double finalAmount) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }
}