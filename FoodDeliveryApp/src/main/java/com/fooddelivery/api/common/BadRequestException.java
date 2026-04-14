package com.fooddelivery.api.common;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(400, message);
    }
}