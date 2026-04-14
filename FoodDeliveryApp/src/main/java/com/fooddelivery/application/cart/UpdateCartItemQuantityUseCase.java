package com.fooddelivery.application.cart;

import com.fooddelivery.application.common.CurrentUserProvider;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.model.Cart;

public class UpdateCartItemQuantityUseCase {

    private final CartRepository cartRepository;
    private final CurrentUserProvider currentUserProvider;

    public UpdateCartItemQuantityUseCase(CartRepository cartRepository,
                                         CurrentUserProvider currentUserProvider) {
        this.cartRepository = cartRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Cart execute(int index, int newQuantity) {
        String ownerId = currentUserProvider.requireCurrentUser().getId();
        Cart cart = cartRepository.getOrCreate(ownerId);
        cart.updateQuantity(index, newQuantity);
        cartRepository.save(ownerId, cart);
        return cart;
    }
}