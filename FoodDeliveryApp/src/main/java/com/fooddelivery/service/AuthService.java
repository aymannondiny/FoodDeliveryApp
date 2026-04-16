package com.fooddelivery.service;

import com.fooddelivery.application.auth.GetCurrentUserUseCase;
import com.fooddelivery.application.auth.LoginUseCase;
import com.fooddelivery.application.auth.LogoutUseCase;
import com.fooddelivery.application.auth.RegisterUserUseCase;
import com.fooddelivery.application.auth.request.LoginCommand;
import com.fooddelivery.application.auth.request.RegisterUserCommand;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.User;

import java.util.Optional;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer auth use cases from AppContext.
 */
@Deprecated
public class AuthService {

    private static AuthService instance;

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    private AuthService() {
        AppContext context = AppContext.create();
        this.registerUserUseCase = context.registerUserUseCase();
        this.loginUseCase = context.loginUseCase();
        this.logoutUseCase = context.logoutUseCase();
        this.getCurrentUserUseCase = context.getCurrentUserUseCase();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User register(String name, String email, String password,
                         String phone, User.Role role) {
        return registerUserUseCase.execute(
                new RegisterUserCommand(name, email, password, phone, role)
        ).getUser();
    }

    public User login(String email, String password) {
        return loginUseCase.execute(
                new LoginCommand(email, password)
        ).getUser();
    }

    public void logout() {
        logoutUseCase.execute();
    }

    public Optional<User> getCurrentUser() {
        return getCurrentUserUseCase.execute();
    }

    public User requireCurrentUser() {
        return getCurrentUserUseCase.requireCurrentUser();
    }

    public boolean isLoggedIn() {
        return getCurrentUserUseCase.isLoggedIn();
    }
}