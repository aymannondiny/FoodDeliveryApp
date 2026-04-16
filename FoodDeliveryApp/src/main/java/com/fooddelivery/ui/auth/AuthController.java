package com.fooddelivery.ui.auth;

import com.fooddelivery.application.auth.GetCurrentUserUseCase;
import com.fooddelivery.application.auth.LoginUseCase;
import com.fooddelivery.application.auth.LogoutUseCase;
import com.fooddelivery.application.auth.RegisterUserUseCase;
import com.fooddelivery.application.auth.request.LoginCommand;
import com.fooddelivery.application.auth.request.RegisterUserCommand;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.auth.request.LoginForm;
import com.fooddelivery.ui.auth.request.RegisterForm;

import java.util.Optional;

/**
 * UI-facing controller for authentication workflows.
 * Keeps Swing panels free from direct use-case orchestration.
 */
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          LogoutUseCase logoutUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    public User register(RegisterForm form) {
        return registerUserUseCase.execute(
                new RegisterUserCommand(
                        form.getName(),
                        form.getEmail(),
                        form.getPassword(),
                        form.getPhone(),
                        form.getRole()
                )
        ).getUser();
    }

    public User login(LoginForm form) {
        return loginUseCase.execute(
                new LoginCommand(
                        form.getEmail(),
                        form.getPassword()
                )
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