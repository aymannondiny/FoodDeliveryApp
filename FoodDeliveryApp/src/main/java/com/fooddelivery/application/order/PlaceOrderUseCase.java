package com.fooddelivery.application.order;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.application.order.request.PlaceOrderCommand;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.domain.repository.CouponRepository;
import com.fooddelivery.domain.repository.MenuItemRepository;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.service.DeliveryFeeCalculator;
import com.fooddelivery.domain.service.OrderPaymentProcessor;
import com.fooddelivery.domain.service.RiderAssigner;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.Coupon;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.Restaurant;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceOrderUseCase {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final CouponRepository couponRepository;
    private final IdGenerator idGenerator;
    private final DeliveryFeeCalculator deliveryFeeCalculator;
    private final OrderPaymentProcessor orderPaymentProcessor;
    private final RiderAssigner riderAssigner;

    public PlaceOrderUseCase(CartRepository cartRepository,
                             OrderRepository orderRepository,
                             MenuItemRepository menuItemRepository,
                             CouponRepository couponRepository,
                             IdGenerator idGenerator,
                             DeliveryFeeCalculator deliveryFeeCalculator,
                             OrderPaymentProcessor orderPaymentProcessor,
                             RiderAssigner riderAssigner) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.couponRepository = couponRepository;
        this.idGenerator = idGenerator;
        this.deliveryFeeCalculator = deliveryFeeCalculator;
        this.orderPaymentProcessor = orderPaymentProcessor;
        this.riderAssigner = riderAssigner;
    }

    public Order execute(PlaceOrderCommand command) {
        Cart cart = cartRepository.getOrCreate(command.getCustomerId());
        Restaurant restaurant = command.getRestaurant();

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty.");
        }

        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant is required.");
        }

        if (cart.getSubtotal() < restaurant.getMinOrderAmount()) {
            throw new IllegalStateException(
                    String.format("Minimum order is %.2f BDT.", restaurant.getMinOrderAmount())
            );
        }

        Order order = new Order(
                idGenerator.nextId("ORD"),
                command.getCustomerId(),
                restaurant.getId(),
                restaurant.getName(),
                command.getDeliveryAddress(),
                command.getPaymentMethod()
        );

        List<OrderItem> items = cart.getItems().stream()
                .map(cartItem -> cartItem.toOrderItem())
                .collect(Collectors.toList());

        order.setItems(items);

        for (OrderItem item : items) {
            menuItemRepository.findById(item.getMenuItemId()).ifPresent(menuItem -> {
                decrementStock(menuItem, item.getQuantity());
                menuItemRepository.save(menuItem.getId(), menuItem);
            });
        }

        double deliveryFee = deliveryFeeCalculator.calculate(restaurant, command.getDeliveryAddress());
        order.setDeliveryFee(deliveryFee);

        double discount = applyCoupon(command.getCouponCode(), cart.getSubtotal(), order);
        order.setDiscount(discount);
        order.recalculateTotals();

        orderRepository.save(order.getId(), order);

        orderPaymentProcessor.process(order);
        riderAssigner.assignTo(order);

        cart.clear();
        cartRepository.save(command.getCustomerId(), cart);

        return order;
    }

    private void decrementStock(MenuItem menuItem, int quantity) {
        menuItem.decrementQuantity(quantity);
    }

    private double applyCoupon(String couponCode, double subtotal, Order order) {
        if (couponCode == null || couponCode.isBlank()) {
            return 0.0;
        }

        try {
            Coupon coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Coupon '" + couponCode + "' not found."));

            if (!coupon.isValid()) {
                throw new IllegalStateException("Coupon '" + couponCode + "' is expired or inactive.");
            }

            if (subtotal < coupon.getMinOrderAmount()) {
                throw new IllegalStateException(String.format(
                        "Minimum order of %.2f BDT required for this coupon.",
                        coupon.getMinOrderAmount()
                ));
            }

            double discount = coupon.calculateDiscount(subtotal);
            if (discount > 0) {
                order.setCouponCode(couponCode);
                coupon.incrementUsage();
                couponRepository.save(coupon.getId(), coupon);
            }

            return discount;
        } catch (Exception e) {
            System.err.println("Coupon skipped: " + e.getMessage());
            return 0.0;
        }
    }
}