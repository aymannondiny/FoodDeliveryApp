package com.fooddelivery.repository;

import com.fooddelivery.model.*;
import com.google.gson.reflect.TypeToken;

import java.util.*;

// ─────────────────────────────────────────────────────────────────────────────
//  UserRepository
// ─────────────────────────────────────────────────────────────────────────────

public class UserRepository extends FileRepository<User> {
    private static UserRepository instance;

    private UserRepository() {
        super("data/users.json", new TypeToken<Map<String, User>>(){}.getType());
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public Optional<User> findByEmail(String email) {
        return findWhere(u -> u.getEmail().equalsIgnoreCase(email)).stream().findFirst();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  RestaurantRepository
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
//  MenuItemRepository
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
//  OrderRepository
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
//  RiderRepository
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
//  CouponRepository
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
//  PaymentRepository
// ─────────────────────────────────────────────────────────────────────────────

