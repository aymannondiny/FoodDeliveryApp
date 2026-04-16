package com.fooddelivery.ui.customer.cart;

import com.fooddelivery.application.auth.GetCurrentUserUseCase;
import com.fooddelivery.application.cart.ClearCartUseCase;
import com.fooddelivery.application.cart.GetCartUseCase;
import com.fooddelivery.application.cart.RemoveCartItemUseCase;
import com.fooddelivery.application.cart.UpdateCartItemQuantityUseCase;
import com.fooddelivery.application.coupon.CouponValidationUseCase;
import com.fooddelivery.application.order.PlaceOrderUseCase;
import com.fooddelivery.application.order.request.PlaceOrderCommand;
import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Coupon;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.ui.customer.cart.request.CheckoutForm;
import com.fooddelivery.ui.customer.cart.viewmodel.CartItemViewModel;
import com.fooddelivery.ui.customer.cart.viewmodel.CartViewModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * UI-facing controller for shopping cart and checkout workflow.
 */
public class CartController {

    private final GetCartUseCase getCartUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CouponValidationUseCase couponValidationUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final RestaurantQueryService restaurantQueryService;
    private final PlaceOrderUseCase placeOrderUseCase;

    public CartController(GetCartUseCase getCartUseCase,
                          UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase,
                          RemoveCartItemUseCase removeCartItemUseCase,
                          ClearCartUseCase clearCartUseCase,
                          CouponValidationUseCase couponValidationUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          RestaurantQueryService restaurantQueryService,
                          PlaceOrderUseCase placeOrderUseCase) {
        this.getCartUseCase = getCartUseCase;
        this.updateCartItemQuantityUseCase = updateCartItemQuantityUseCase;
        this.removeCartItemUseCase = removeCartItemUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.couponValidationUseCase = couponValidationUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.restaurantQueryService = restaurantQueryService;
        this.placeOrderUseCase = placeOrderUseCase;
    }

    public CartViewModel loadCartView() {
        Cart cart = getCartUseCase.execute();

        List<CartItemViewModel> items = IntStream.range(0, cart.getItems().size())
                .mapToObj(index -> toItemViewModel(index, cart.getItems().get(index)))
                .collect(Collectors.toList());

        return new CartViewModel(
                cart.isEmpty(),
                cart.getRestaurantName(),
                items,
                cart.getSubtotal(),
                cart.getTotalItems()
        );
    }

    public void updateQuantity(int index, int newQuantity) {
        updateCartItemQuantityUseCase.execute(index, newQuantity);
    }

    public void removeItem(int index) {
        removeCartItemUseCase.execute(index);
    }

    public void clearCart() {
        clearCartUseCase.execute();
    }

    public double previewCouponDiscount(String code) {
        String normalized = code != null ? code.trim() : "";
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Coupon code is required.");
        }

        Cart cart = getCartUseCase.execute();
        Coupon coupon = couponValidationUseCase.execute(normalized, cart.getSubtotal());
        return coupon.calculateDiscount(cart.getSubtotal());
    }

    public String getDefaultArea() {
        return getCurrentUserUseCase.execute()
                .map(User::getDefaultAddress)
                .map(Address::getArea)
                .orElse("");
    }

    public Order placeOrder(CheckoutForm form, String couponCode) {
        Cart cart = getCartUseCase.execute();
        if (cart.isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        String restaurantId = cart.getRestaurantId();
        if (restaurantId == null || restaurantId.isBlank()) {
            throw new IllegalStateException("No restaurant selected for this cart.");
        }

        User user = getCurrentUserUseCase.requireCurrentUser();

        Restaurant restaurant = restaurantQueryService.findById(restaurantId)
                .orElseThrow(() -> new IllegalStateException("Restaurant not found."));

        Address address = new Address(
                form.getStreet(),
                form.getArea(),
                form.getCity(),
                ""
        );

        return placeOrderUseCase.execute(
                new PlaceOrderCommand(
                        user.getId(),
                        restaurant,
                        address,
                        form.getPaymentMethod(),
                        couponCode
                )
        );
    }

    private CartItemViewModel toItemViewModel(int index, CartItem item) {
        return new CartItemViewModel(
                index,
                item.getMenuItemName(),
                String.format("%.2f BDT each", item.getUnitPrice()),
                item.getQuantity(),
                String.format("%.2f BDT", item.getLineTotal())
        );
    }
}