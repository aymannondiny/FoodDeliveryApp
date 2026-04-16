package com.fooddelivery.api.common;

import java.util.Map;

public final class RequestParams {

    private RequestParams() {
    }

    public static String required(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null || value.isBlank()) {
            throw new BadRequestException(key + " query parameter is required.");
        }
        return value;
    }

    public static double requiredDouble(Map<String, String> params, String key) {
        String value = required(params, key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid " + key + ".");
        }
    }
}