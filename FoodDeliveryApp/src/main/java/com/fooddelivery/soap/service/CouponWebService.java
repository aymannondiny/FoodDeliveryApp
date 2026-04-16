package com.fooddelivery.soap.service;

import com.fooddelivery.application.coupon.CouponValidationUseCase;
import com.fooddelivery.model.Coupon;
import com.fooddelivery.soap.dto.SoapCouponResult;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(serviceName = "CouponService", targetNamespace = "http://fooddelivery.com/soap")
public class CouponWebService {

    private final CouponValidationUseCase couponValidationUseCase;

    public CouponWebService(CouponValidationUseCase couponValidationUseCase) {
        this.couponValidationUseCase = couponValidationUseCase;
    }

    @WebMethod
    public SoapCouponResult validateCoupon(@WebParam(name = "code") String code,
                                           @WebParam(name = "subtotal") double subtotal) {
        Coupon coupon = couponValidationUseCase.execute(code, subtotal);
        double discount = coupon.calculateDiscount(subtotal);

        SoapCouponResult result = new SoapCouponResult();
        result.setCode(coupon.getCode());
        result.setDiscountPercent(coupon.getDiscountPercent());
        result.setDiscountAmount(discount);
        result.setFinalAmount(subtotal - discount);

        return result;
    }
}