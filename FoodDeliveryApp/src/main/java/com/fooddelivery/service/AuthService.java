package com.fooddelivery.service;

import com.fooddelivery.model.User;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.util.Optional;

/**
 * Handles user registration, login, and session management.
 */
public class AuthService {

    private static AuthService instance;
    private User currentUser; // Simple in-memory session

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    /**
     * Register a new user.
     * @return the created User, or throws if email already exists.
     */
    public User register(String name, String email, String password,
                         String phone, User.Role role) {
        if (!AppUtils.isValidEmail(email))
            throw new IllegalArgumentException("Invalid email address.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (RepositoryFactory.users().findByEmail(email).isPresent())
            throw new IllegalStateException("An account with this email already exists.");

        User user = new User(
            AppUtils.generateId("USR"),
            name, email,
            AppUtils.hashPassword(password),
            phone, role
        );
        RepositoryFactory.users().save(user.getId(), user);
        return user;
    }

    /**
     * Authenticate a user by email + password.
     * Sets the current session user on success.
     */
    public User login(String email, String password) {
        User user = RepositoryFactory.users()
                        .findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("No account found for: " + email));

        if (!user.getPasswordHash().equals(AppUtils.hashPassword(password)))
            throw new IllegalArgumentException("Incorrect password.");
        if (!user.isActive())
            throw new IllegalStateException("This account has been deactivated.");

        this.currentUser = user;
        return user;
    }

    public void logout() { this.currentUser = null; }

    public Optional<User> getCurrentUser() { return Optional.ofNullable(currentUser); }

    public User requireCurrentUser() {
        return getCurrentUser().orElseThrow(() ->
            new IllegalStateException("Not logged in."));
    }

    public boolean isLoggedIn() { return currentUser != null; }
}
