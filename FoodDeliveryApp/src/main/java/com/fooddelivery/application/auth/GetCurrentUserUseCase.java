package com.fooddelivery.application.auth;

import com.fooddelivery.application.common.CurrentUserProvider;
import com.fooddelivery.model.User;

import java.util.Optional;

public class GetCurrentUserUseCase {

    private final CurrentUserProvider currentUserProvider;

    public GetCurrentUserUseCase(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public Optional<User> execute() {
        return currentUserProvider.getCurrentUser();
    }

    public User requireCurrentUser() {
        return currentUserProvider.requireCurrentUser();
    }

    public boolean isLoggedIn() {
        return currentUserProvider.isLoggedIn();
    }
}