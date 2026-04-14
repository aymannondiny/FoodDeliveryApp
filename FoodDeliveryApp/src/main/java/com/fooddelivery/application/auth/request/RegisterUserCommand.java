package com.fooddelivery.application.auth.request;

import com.fooddelivery.model.User;

public class RegisterUserCommand {

    private final String name;
    private final String email;
    private final String password;
    private final String phone;
    private final User.Role role;

    public RegisterUserCommand(String name, String email, String password,
                               String phone, User.Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public User.Role getRole() {
        return role;
    }
}