package com.fooddelivery.service;

import com.fooddelivery.model.Coupon;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Manages discount coupons: creation, validation, and discount calculation.
 */
public class CouponService {

    private static CouponService instance;

    private CouponService() {}

    public static synchronized CouponService getInstance() {
        if (instance == null) instance = new CouponService();
        return instance;
    }

    public Coupon createCoupon(String code, double discountPercent,
                               double maxDiscountAmount, double minOrderAmount,
                               LocalDate expiryDate, int usageLimit) {
        if (RepositoryFactory.coupons().findByCode(code).isPresent())
            throw new IllegalStateException("Coupon code '" + code + "' already exists.");

        Coupon coupon = new Coupon(
            AppUtils.generateId("CPN"), code.toUpperCase(),
            discountPercent, maxDiscountAmount, minOrderAmount, expiryDate
        );
        coupon.setUsageLimit(usageLimit);
        RepositoryFactory.coupons().save(coupon.getId(), coupon);
        return coupon;
    }

    /**
     * Validate a coupon code against the current subtotal.
     * @return the Coupon object if valid, otherwise throws.
     */
    public Coupon validateCoupon(String code, double subtotal) {
        Coupon coupon = RepositoryFactory.coupons().findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Coupon '" + code + "' not found."));

        if (!coupon.isValid())
            throw new IllegalStateException("Coupon '" + code + "' is expired or inactive.");
        if (subtotal < coupon.getMinOrderAmount())
            throw new IllegalStateException(String.format(
                "Minimum order of %.2f BDT required for this coupon.", coupon.getMinOrderAmount()));
        return coupon;
    }

    public double calculateDiscount(String code, double subtotal) {
        return RepositoryFactory.coupons()
                   .findByCode(code)
                   .map(c -> c.calculateDiscount(subtotal))
                   .orElse(0.0);
    }

    public void markUsed(String couponId) {
        RepositoryFactory.coupons().findById(couponId).ifPresent(c -> {
            c.incrementUsage();
            RepositoryFactory.coupons().save(couponId, c);
        });
    }

    public List<Coupon> getAllCoupons() {
        return RepositoryFactory.coupons().findAll();
    }
}
