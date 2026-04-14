package com.fooddelivery.application.common;

import com.fooddelivery.model.User;

import java.util.Optional;

public interface CurrentUserProvider {

    Optional<User> getCurrentUser();

    default User requireCurrentUser() {
        return getCurrentUser().orElseThrow(() ->
                new IllegalStateException("Not logged in."));
    }

    default boolean isLoggedIn() {
        return getCurrentUser().isPresent();
    }
}