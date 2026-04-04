package com.fooddelivery.repository;

import com.fooddelivery.model.Coupon;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Optional;

public class CouponRepository extends FileRepository<Coupon> {
    private static CouponRepository instance;

    private CouponRepository() {
        super("data/coupons.json", new TypeToken<Map<String, Coupon>>(){}.getType());
    }

    public static synchronized CouponRepository getInstance() {
        if (instance == null) instance = new CouponRepository();
        return instance;
    }

    public Optional<Coupon> findByCode(String code) {
        return findWhere(c -> c.getCode().equalsIgnoreCase(code)).stream().findFirst();
    }
}
