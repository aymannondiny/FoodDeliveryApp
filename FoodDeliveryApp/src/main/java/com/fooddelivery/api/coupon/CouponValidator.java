package com.fooddelivery.api.coupon;

public interface CouponValidator {
    CouponValidationResult validate(String code, double subtotal);
}