package com.fooddelivery.application.coupon;

import com.fooddelivery.domain.repository.CouponRepository;
import com.fooddelivery.model.Coupon;

public class CouponValidationUseCase {

    private final CouponRepository couponRepository;

    public CouponValidationUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Coupon execute(String code, double subtotal) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Coupon '" + code + "' not found."));

        if (!coupon.isValid()) {
            throw new IllegalStateException("Coupon '" + code + "' is expired or inactive.");
        }

        if (subtotal < coupon.getMinOrderAmount()) {
            throw new IllegalStateException(String.format(
                    "Minimum order of %.2f BDT required for this coupon.",
                    coupon.getMinOrderAmount()
            ));
        }

        return coupon;
    }
}