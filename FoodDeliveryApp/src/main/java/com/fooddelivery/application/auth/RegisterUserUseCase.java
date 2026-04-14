package com.fooddelivery.application.auth;

import com.fooddelivery.application.auth.request.RegisterUserCommand;
import com.fooddelivery.application.auth.result.AuthResult;
import com.fooddelivery.application.common.EmailValidator;
import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.application.common.PasswordHasher;
import com.fooddelivery.domain.repository.UserRepository;
import com.fooddelivery.model.User;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final PasswordHasher passwordHasher;
    private final IdGenerator idGenerator;

    public RegisterUserUseCase(UserRepository userRepository,
                               EmailValidator emailValidator,
                               PasswordHasher passwordHasher,
                               IdGenerator idGenerator) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
        this.passwordHasher = passwordHasher;
        this.idGenerator = idGenerator;
    }

    public AuthResult execute(RegisterUserCommand command) {
        String email = command.getEmail();

        if (!emailValidator.isValid(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }

        if (command.getPassword() == null || command.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        User user = new User(
                idGenerator.nextId("USR"),
                command.getName(),
                email,
                passwordHasher.hash(command.getPassword()),
                command.getPhone(),
                command.getRole()
        );

        userRepository.save(user.getId(), user);
        return new AuthResult(user);
    }
}