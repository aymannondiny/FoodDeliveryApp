package com.fooddelivery.domain.repository;

import com.fooddelivery.model.Coupon;

import java.util.Optional;

public interface CouponRepository extends DataRepository<Coupon> {
    Optional<Coupon> findByCode(String code);
}