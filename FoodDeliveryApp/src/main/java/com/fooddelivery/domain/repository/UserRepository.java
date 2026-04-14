package com.fooddelivery.domain.repository;

import com.fooddelivery.model.User;

import java.util.Optional;

public interface UserRepository extends DataRepository<User> {
    Optional<User> findByEmail(String email);
}