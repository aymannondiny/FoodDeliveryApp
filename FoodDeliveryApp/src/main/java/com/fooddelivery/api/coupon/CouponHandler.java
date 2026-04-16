package com.fooddelivery.api.coupon;

import com.fooddelivery.api.common.BaseHandler;
import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.api.common.RequestParams;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class CouponHandler extends BaseHandler {

    private final CouponValidator couponValidator;
    private final Mapper<CouponValidationResult, Map<String, Object>> couponMapper;

    public CouponHandler(CouponValidator couponValidator,
                         Mapper<CouponValidationResult, Map<String, Object>> couponMapper) {
        this.couponValidator = couponValidator;
        this.couponMapper = couponMapper;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange, Map<String, String> params) {
        String code = RequestParams.required(params, "code");
        double subtotal = RequestParams.requiredDouble(params, "subtotal");

        CouponValidationResult result = couponValidator.validate(code, subtotal);
        return couponMapper.map(result);
    }
}