package com.fooddelivery.ui.customer.menu;

import com.fooddelivery.application.cart.AddCartItemUseCase;
import com.fooddelivery.application.cart.GetCartUseCase;
import com.fooddelivery.application.cart.request.AddCartItemCommand;
import com.fooddelivery.application.menu.MenuQueryService;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.ui.customer.menu.viewmodel.MenuCategoryViewModel;
import com.fooddelivery.ui.customer.menu.viewmodel.MenuItemViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UI-facing controller for restaurant menu display and add-to-cart workflow.
 */
public class MenuController {

    private final MenuQueryService menuQueryService;
    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;

    public MenuController(MenuQueryService menuQueryService,
                          GetCartUseCase getCartUseCase,
                          AddCartItemUseCase addCartItemUseCase) {
        this.menuQueryService = menuQueryService;
        this.getCartUseCase = getCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
    }

    public List<MenuCategoryViewModel> loadMenu(String restaurantId) {
        Map<String, List<MenuItem>> groupedMenu = menuQueryService.getMenuByCategory(restaurantId);

        return groupedMenu.entrySet().stream()
                .map(entry -> new MenuCategoryViewModel(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(this::toItemViewModel)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public boolean hasItemsFromAnotherRestaurant(String restaurantId) {
        Cart cart = getCartUseCase.execute();
        return cart.getRestaurantId() != null
                && !cart.getRestaurantId().equals(restaurantId)
                && !cart.isEmpty();
    }

    public void addToCart(Restaurant restaurant,
                          MenuItem item,
                          int quantity,
                          List<MenuItemAddon> selectedAddons,
                          String specialInstructions) {
        addCartItemUseCase.execute(
                new AddCartItemCommand(
                        item,
                        quantity,
                        selectedAddons,
                        specialInstructions,
                        restaurant.getName()
                )
        );
    }

    private MenuItemViewModel toItemViewModel(MenuItem item) {
        String description = item.getDescription() != null ? item.getDescription() : "";
        String stock = item.getQuantity() == -1 ? "" : "  (Stock: " + item.getQuantity() + ")";
        String priceText = String.format("%.0f BDT", item.getPrice()) + stock;

        return new MenuItemViewModel(
                item,
                item.getName(),
                description,
                priceText,
                item.isOrderable()
        );
    }
}