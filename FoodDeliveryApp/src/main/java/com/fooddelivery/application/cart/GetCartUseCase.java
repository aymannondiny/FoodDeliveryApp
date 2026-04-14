package com.fooddelivery.application.cart;

import com.fooddelivery.application.common.CurrentUserProvider;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.model.Cart;

public class GetCartUseCase {

    private final CartRepository cartRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetCartUseCase(CartRepository cartRepository,
                          CurrentUserProvider currentUserProvider) {
        this.cartRepository = cartRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Cart execute() {
        String ownerId = currentUserProvider.requireCurrentUser().getId();
        return cartRepository.getOrCreate(ownerId);
    }
}