package com.fooddelivery.application.auth.request;

public class LoginCommand {

    private final String email;
    private final String password;

    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}