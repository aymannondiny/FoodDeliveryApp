package com.fooddelivery.application.common;

public interface PasswordHasher {
    String hash(String plainText);
}