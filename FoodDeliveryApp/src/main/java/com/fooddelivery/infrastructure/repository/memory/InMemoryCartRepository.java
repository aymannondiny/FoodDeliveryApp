package com.fooddelivery.infrastructure.repository.memory;

import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.model.Cart;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCartRepository implements CartRepository {

    private final Map<String, Cart> cartsByOwnerId = new ConcurrentHashMap<>();

    @Override
    public Cart getOrCreate(String ownerId) {
        return cartsByOwnerId.computeIfAbsent(ownerId, Cart::new);
    }

    @Override
    public Optional<Cart> findByOwnerId(String ownerId) {
        return Optional.ofNullable(cartsByOwnerId.get(ownerId));
    }

    @Override
    public void save(String ownerId, Cart cart) {
        cartsByOwnerId.put(ownerId, cart);
    }

    @Override
    public void deleteByOwnerId(String ownerId) {
        cartsByOwnerId.remove(ownerId);
    }
}