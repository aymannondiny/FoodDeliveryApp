package com.fooddelivery.application.cart;

import com.fooddelivery.application.common.CurrentUserProvider;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.model.Cart;

public class ClearCartUseCase {

    private final CartRepository cartRepository;
    private final CurrentUserProvider currentUserProvider;

    public ClearCartUseCase(CartRepository cartRepository,
                            CurrentUserProvider currentUserProvider) {
        this.cartRepository = cartRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Cart execute() {
        String ownerId = currentUserProvider.requireCurrentUser().getId();
        Cart cart = cartRepository.getOrCreate(ownerId);
        cart.clear();
        cartRepository.save(ownerId, cart);
        return cart;
    }
}