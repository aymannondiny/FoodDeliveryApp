package com.fooddelivery.infrastructure.security;

import com.fooddelivery.application.common.PasswordHasher;
import com.fooddelivery.util.AppUtils;

public class Sha256PasswordHasher implements PasswordHasher {

    @Override
    public String hash(String plainText) {
        return AppUtils.hashPassword(plainText);
    }
}