package com.fooddelivery.application.auth;

import com.fooddelivery.application.auth.request.LoginCommand;
import com.fooddelivery.application.auth.result.AuthResult;
import com.fooddelivery.application.common.PasswordHasher;
import com.fooddelivery.domain.repository.UserRepository;
import com.fooddelivery.infrastructure.session.CurrentSession;
import com.fooddelivery.model.User;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentSession currentSession;

    public LoginUseCase(UserRepository userRepository,
                        PasswordHasher passwordHasher,
                        CurrentSession currentSession) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.currentSession = currentSession;
    }

    public AuthResult execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("No account found for: " + command.getEmail()));

        if (!user.getPasswordHash().equals(passwordHasher.hash(command.getPassword()))) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("This account has been deactivated.");
        }

        currentSession.setCurrentUser(user);
        return new AuthResult(user);
    }
}