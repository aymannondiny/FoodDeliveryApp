package com.fooddelivery.service;

import com.fooddelivery.application.auth.GetCurrentUserUseCase;
import com.fooddelivery.application.cart.AddCartItemUseCase;
import com.fooddelivery.application.cart.ClearCartUseCase;
import com.fooddelivery.application.cart.GetCartUseCase;
import com.fooddelivery.application.cart.RemoveCartItemUseCase;
import com.fooddelivery.application.cart.UpdateCartItemQuantityUseCase;
import com.fooddelivery.application.cart.request.AddCartItemCommand;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer cart use cases from AppContext.
 */
public class CartService {

    private static CartService instance;

    private final CartRepository cartRepository;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final ClearCartUseCase clearCartUseCase;

    private CartService() {
        AppContext context = AppContext.create();
        this.cartRepository = context.cartRepository();
        this.getCurrentUserUseCase = context.getCurrentUserUseCase();
        this.getCartUseCase = context.getCartUseCase();
        this.addCartItemUseCase = context.addCartItemUseCase();
        this.removeCartItemUseCase = context.removeCartItemUseCase();
        this.updateCartItemQuantityUseCase = context.updateCartItemQuantityUseCase();
        this.clearCartUseCase = context.clearCartUseCase();
    }

    public static synchronized CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    public void addItem(MenuItem menuItem, int quantity,
                        List<MenuItemAddon> selectedAddons, String specialInstructions) {
        addItem(menuItem, quantity, selectedAddons, specialInstructions, null);
    }

    /**
     * Preferred migration overload.
     */
    public void addItem(MenuItem menuItem, int quantity,
                        List<MenuItemAddon> selectedAddons,
                        String specialInstructions,
                        String restaurantName) {
        addCartItemUseCase.execute(
                new AddCartItemCommand(
                        menuItem,
                        quantity,
                        selectedAddons,
                        specialInstructions,
                        restaurantName
                )
        );
    }

    public void removeItem(int index) {
        if (getCurrentUserUseCase.execute().isPresent()) {
            removeCartItemUseCase.execute(index);
        }
    }

    public void updateQuantity(int index, int newQuantity) {
        if (getCurrentUserUseCase.execute().isPresent()) {
            updateCartItemQuantityUseCase.execute(index, newQuantity);
        }
    }

    public void clear() {
        if (getCurrentUserUseCase.execute().isPresent()) {
            clearCartUseCase.execute();
        }
    }

    public boolean isEmpty() {
        return getCurrentCartOptional().map(Cart::isEmpty).orElse(true);
    }

    public double getSubtotal() {
        return getCurrentCartOptional().map(Cart::getSubtotal).orElse(0.0);
    }

    public int getTotalItems() {
        return getCurrentCartOptional().map(Cart::getTotalItems).orElse(0);
    }

    public String getRestaurantId() {
        return getCurrentCartOptional().map(Cart::getRestaurantId).orElse(null);
    }

    public void setRestaurantId(String id) {
        updateCart(cart -> cart.setRestaurantId(id));
    }

    public String getRestaurantName() {
        return getCurrentCartOptional().map(Cart::getRestaurantName).orElse(null);
    }

    public void setRestaurantName(String name) {
        updateCart(cart -> cart.setRestaurantName(name));
    }

    public List<OrderItem> getItems() {
        List<OrderItem> items = getCurrentCartOptional()
                .map(cart -> cart.getItems().stream()
                        .map(CartItem::toOrderItem)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public Cart getCart() {
        return getCartUseCase.execute();
    }

    private Optional<Cart> getCurrentCartOptional() {
        Optional<User> userOpt = getCurrentUserUseCase.execute();
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        return cartRepository.findByOwnerId(userOpt.get().getId());
    }

    private void updateCart(java.util.function.Consumer<Cart> consumer) {
        Optional<User> userOpt = getCurrentUserUseCase.execute();
        if (userOpt.isEmpty()) {
            return;
        }

        String ownerId = userOpt.get().getId();
        Cart cart = cartRepository.getOrCreate(ownerId);
        consumer.accept(cart);
        cartRepository.save(ownerId, cart);
    }
}