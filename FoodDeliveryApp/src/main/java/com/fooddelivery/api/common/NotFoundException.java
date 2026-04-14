package com.fooddelivery.api.common;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(404, message);
    }
}