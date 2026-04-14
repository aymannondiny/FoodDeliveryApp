package com.fooddelivery.repository;

import com.fooddelivery.model.Coupon;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Optional;

public class CouponRepository extends FileRepository<Coupon>
        implements com.fooddelivery.domain.repository.CouponRepository {

    private static CouponRepository instance;

    private CouponRepository() {
        super("data/coupons.json", new TypeToken<Map<String, Coupon>>() {}.getType());
    }

    public static synchronized CouponRepository getInstance() {
        if (instance == null) {
            instance = new CouponRepository();
        }
        return instance;
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return findWhere(coupon ->
                coupon.getCode() != null && coupon.getCode().equalsIgnoreCase(code))
                .stream()
                .findFirst();
    }
}