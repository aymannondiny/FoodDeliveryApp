package com.fooddelivery.api.coupon;

import com.fooddelivery.api.common.Mapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class CouponResponseMapper implements Mapper<CouponValidationResult, Map<String, Object>> {

    @Override
    public Map<String, Object> map(CouponValidationResult result) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", result.getCode());
        m.put("discountPercent", result.getDiscountPercent());
        m.put("discountAmount", result.getDiscountAmount());
        m.put("finalAmount", result.getFinalAmount());
        return m;
    }
}