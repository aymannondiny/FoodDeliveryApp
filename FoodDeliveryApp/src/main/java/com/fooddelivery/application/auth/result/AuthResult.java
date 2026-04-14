package com.fooddelivery.application.auth.result;

import com.fooddelivery.model.User;

public class AuthResult {

    private final User user;

    public AuthResult(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}