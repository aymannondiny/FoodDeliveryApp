package com.fooddelivery.application.coupon;

import com.fooddelivery.domain.repository.CouponRepository;
import com.fooddelivery.model.Coupon;

import java.util.List;

public class CouponQueryService {

    private final CouponRepository couponRepository;

    public CouponQueryService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public double calculateDiscount(String code, double subtotal) {
        return couponRepository.findByCode(code)
                .map(coupon -> coupon.calculateDiscount(subtotal))
                .orElse(0.0);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
}