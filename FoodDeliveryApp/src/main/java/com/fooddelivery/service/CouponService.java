package com.fooddelivery.service;

import com.fooddelivery.application.coupon.CouponCommandService;
import com.fooddelivery.application.coupon.CouponQueryService;
import com.fooddelivery.application.coupon.CouponValidationUseCase;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.Coupon;

import java.time.LocalDate;
import java.util.List;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer coupon application services from AppContext.
 */
public class CouponService {

    private static CouponService instance;

    private final CouponCommandService commandService;
    private final CouponQueryService queryService;
    private final CouponValidationUseCase validationUseCase;

    private CouponService() {
        AppContext context = AppContext.create();
        this.commandService = context.couponCommandService();
        this.queryService = context.couponQueryService();
        this.validationUseCase = context.couponValidationUseCase();
    }

    public static synchronized CouponService getInstance() {
        if (instance == null) {
            instance = new CouponService();
        }
        return instance;
    }

    public Coupon createCoupon(String code, double discountPercent,
                               double maxDiscountAmount, double minOrderAmount,
                               LocalDate expiryDate, int usageLimit) {
        return commandService.createCoupon(
                code,
                discountPercent,
                maxDiscountAmount,
                minOrderAmount,
                expiryDate,
                usageLimit
        );
    }

    public Coupon validateCoupon(String code, double subtotal) {
        return validationUseCase.execute(code, subtotal);
    }

    public double calculateDiscount(String code, double subtotal) {
        return queryService.calculateDiscount(code, subtotal);
    }

    public void markUsed(String couponId) {
        commandService.markUsed(couponId);
    }

    public List<Coupon> getAllCoupons() {
        return queryService.getAllCoupons();
    }
}