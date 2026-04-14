package com.fooddelivery.application.coupon;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.domain.repository.CouponRepository;
import com.fooddelivery.model.Coupon;

import java.time.LocalDate;

public class CouponCommandService {

    private final CouponRepository couponRepository;
    private final IdGenerator idGenerator;

    public CouponCommandService(CouponRepository couponRepository,
                                IdGenerator idGenerator) {
        this.couponRepository = couponRepository;
        this.idGenerator = idGenerator;
    }

    public Coupon createCoupon(String code,
                               double discountPercent,
                               double maxDiscountAmount,
                               double minOrderAmount,
                               LocalDate expiryDate,
                               int usageLimit) {
        if (couponRepository.findByCode(code).isPresent()) {
            throw new IllegalStateException("Coupon code '" + code + "' already exists.");
        }

        Coupon coupon = new Coupon(
                idGenerator.nextId("CPN"),
                code.toUpperCase(),
                discountPercent,
                maxDiscountAmount,
                minOrderAmount,
                expiryDate
        );

        coupon.setUsageLimit(usageLimit);
        couponRepository.save(coupon.getId(), coupon);
        return coupon;
    }

    public void markUsed(String couponId) {
        couponRepository.findById(couponId).ifPresent(coupon -> {
            coupon.incrementUsage();
            couponRepository.save(couponId, coupon);
        });
    }
}