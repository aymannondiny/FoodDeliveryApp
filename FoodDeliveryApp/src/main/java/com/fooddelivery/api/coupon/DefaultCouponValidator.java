package com.fooddelivery.api.coupon;

import com.fooddelivery.api.common.BadRequestException;
import com.fooddelivery.application.coupon.CouponValidationUseCase;
import com.fooddelivery.model.Coupon;

public class DefaultCouponValidator implements CouponValidator {

    private final CouponValidationUseCase couponValidationUseCase;

    public DefaultCouponValidator(CouponValidationUseCase couponValidationUseCase) {
        this.couponValidationUseCase = couponValidationUseCase;
    }

    @Override
    public CouponValidationResult validate(String code, double subtotal) {
        try {
            Coupon coupon = couponValidationUseCase.execute(code, subtotal);
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