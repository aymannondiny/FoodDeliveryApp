package com.fooddelivery.infrastructure.security;

import com.fooddelivery.application.common.EmailValidator;
import com.fooddelivery.util.AppUtils;

public class RegexEmailValidator implements EmailValidator {

    @Override
    public boolean isValid(String email) {
        return AppUtils.isValidEmail(email);
    }
}