package com.fooddelivery.domain.repository;

import com.fooddelivery.model.Cart;

import java.util.Optional;

public interface CartRepository {

    Cart getOrCreate(String ownerId);

    Optional<Cart> findByOwnerId(String ownerId);

    void save(String ownerId, Cart cart);

    void deleteByOwnerId(String ownerId);
}