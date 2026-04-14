package com.fooddelivery.api.coupon;

import com.fooddelivery.api.common.BadRequestException;
import com.fooddelivery.model.Coupon;
import com.fooddelivery.service.CouponService;

public class DefaultCouponValidator implements CouponValidator {

    private final CouponService couponService;

    public DefaultCouponValidator(CouponService couponService) {
        this.couponService = couponService;
    }

    @Override
    public CouponValidationResult validate(String code, double subtotal) {
        try {
            Coupon coupon = couponService.validateCoupon(code, subtotal);
            double discount = coupon.calculateDiscount(subtotal);

            return new CouponValidationResult(
                    coupon.getCode(),
                    coupon.getDiscountPercent(),
                    subtotal,
                    discount,
                    subtotal - discount
            );
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}