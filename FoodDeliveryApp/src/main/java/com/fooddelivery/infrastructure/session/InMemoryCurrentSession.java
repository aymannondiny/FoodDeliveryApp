package com.fooddelivery.infrastructure.session;

import com.fooddelivery.model.User;

import java.util.Optional;

public class InMemoryCurrentSession implements CurrentSession {

    private volatile User currentUser;

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void clear() {
        this.currentUser = null;
    }
}