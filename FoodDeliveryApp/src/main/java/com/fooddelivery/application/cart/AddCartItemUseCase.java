package com.fooddelivery.application.cart;

import com.fooddelivery.application.cart.request.AddCartItemCommand;
import com.fooddelivery.application.common.CurrentUserProvider;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.model.Cart;

public class AddCartItemUseCase {

    private final CartRepository cartRepository;
    private final CurrentUserProvider currentUserProvider;

    public AddCartItemUseCase(CartRepository cartRepository,
                              CurrentUserProvider currentUserProvider) {
        this.cartRepository = cartRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Cart execute(AddCartItemCommand command) {
        String ownerId = currentUserProvider.requireCurrentUser().getId();
        Cart cart = cartRepository.getOrCreate(ownerId);

        cart.addItem(
                command.getMenuItem(),
                command.getQuantity(),
                command.getSelectedAddons(),
                command.getSpecialInstructions(),
                command.getRestaurantName()
        );

        cartRepository.save(ownerId, cart);
        return cart;
    }
}