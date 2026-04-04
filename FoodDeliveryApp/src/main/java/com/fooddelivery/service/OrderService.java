package com.fooddelivery.service;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core service for order lifecycle management:
 * placement, status progression, cancellation, and history.
 */
public class OrderService {

    private static OrderService instance;

    private OrderService() {}

    public static synchronized OrderService getInstance() {
        if (instance == null) instance = new OrderService();
        return instance;
    }

    // ── Order Placement ──────────────────────────────────────────────────────

    /**
     * Create and persist an order from the current cart contents.
     *
     * @param customerId      Customer placing the order
     * @param restaurant      Target restaurant
     * @param deliveryAddress Where to deliver
     * @param paymentMethod   Chosen payment method
     * @param couponCode      Optional coupon code (null if none)
     * @return The newly created Order
     */
    public Order placeOrder(String customerId, Restaurant restaurant,
                            Address deliveryAddress,
                            Order.PaymentMethod paymentMethod,
                            String couponCode) {

        CartService cart = CartService.getInstance();
        if (cart.isEmpty())
            throw new IllegalStateException("Cart is empty.");
        if (cart.getSubtotal() < restaurant.getMinOrderAmount())
            throw new IllegalStateException(String.format(
                "Minimum order is %.2f BDT.", restaurant.getMinOrderAmount()));

        // Build the order
        Order order = new Order(
            AppUtils.generateId("ORD"),
            customerId, restaurant.getId(), restaurant.getName(),
            deliveryAddress, paymentMethod
        );

        // Copy cart items and decrement stock
        List<OrderItem> items = new ArrayList<>(cart.getItems());
        order.setItems(items);
        items.forEach(item ->
            MenuService.getInstance().decrementStock(item.getMenuItemId(), item.getQuantity()));

        // Delivery fee based on distance
        double distance = restaurant.getAddress().distanceTo(deliveryAddress);
        double deliveryFee = Math.max(30, restaurant.getDeliveryFeePerKm() * Math.max(1, distance));

        // This prevents Infinity and keeps the JSON valid.
        if (deliveryFee > 150 || Double.isInfinite(deliveryFee)) {
            deliveryFee = 150;
        }

        order.setDeliveryFee(deliveryFee);

        // Coupon discount
        double discount = 0;
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                Coupon coupon = CouponService.getInstance().validateCoupon(couponCode, cart.getSubtotal());
                discount = coupon.calculateDiscount(cart.getSubtotal());
                order.setCouponCode(couponCode);
                CouponService.getInstance().markUsed(coupon.getId());
            } catch (Exception e) {
                // Coupon invalid – proceed without discount
                System.err.println("Coupon skipped: " + e.getMessage());
            }
        }
        order.setDiscount(discount);
        order.recalculateTotals();

        RepositoryFactory.orders().save(order.getId(), order);

        // Process payment
        PaymentService.getInstance().processPayment(order);

        // Assign a free rider automatically
        assignRider(order);

        cart.clear();
        return order;
    }

    // ── Status Management ────────────────────────────────────────────────────

    public Order advanceStatus(String orderId, OrderStatus newStatus) {
        Order order = requireOrder(orderId);
        order.advanceStatus(newStatus);
        RepositoryFactory.orders().save(orderId, order);
        return order;
    }

    public Order cancelOrder(String orderId) {
        Order order = requireOrder(orderId);
        if (!order.isCancellable())
            throw new IllegalStateException("Order cannot be cancelled at this stage.");
        order.advanceStatus(OrderStatus.CANCELLED);
        RepositoryFactory.orders().save(orderId, order);
        return order;
    }

    // ── Rider Assignment ─────────────────────────────────────────────────────

    public void assignRider(Order order) {
        List<Rider> available = RepositoryFactory.riders().findAvailable();
        if (available.isEmpty()) return; // No rider – manual assignment later

        Rider rider = available.get(0);
        rider.setCurrentOrderId(order.getId());
        rider.setAvailable(false);
        RepositoryFactory.riders().save(rider.getId(), rider);

        order.setRiderId(rider.getId());
        RepositoryFactory.orders().save(order.getId(), order);
    }

    public void completeDelivery(String orderId) {
        Order order = requireOrder(orderId);
        order.advanceStatus(OrderStatus.DELIVERED);
        RepositoryFactory.orders().save(orderId, order);

        // Free the rider
        if (order.getRiderId() != null) {
            RepositoryFactory.riders().findById(order.getRiderId()).ifPresent(r -> {
                r.setCurrentOrderId(null);
                r.setAvailable(true);
                r.setTotalDeliveries(r.getTotalDeliveries() + 1);
                RepositoryFactory.riders().save(r.getId(), r);
            });
        }
        PaymentService.getInstance().markCodCollected(orderId);
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public Optional<Order> findById(String id) {
        return RepositoryFactory.orders().findById(id);
    }

    public List<Order> getOrderHistory(String customerId) {
        return RepositoryFactory.orders().findByCustomer(customerId).stream()
            .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public List<Order> getRestaurantOrders(String restaurantId) {
        return RepositoryFactory.orders().findByRestaurant(restaurantId).stream()
            .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public List<Order> getActiveRestaurantOrders(String restaurantId) {
        return RepositoryFactory.orders().findActiveByRestaurant(restaurantId).stream()
            .sorted(Comparator.comparing(Order::getCreatedAt))
            .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Order requireOrder(String id) {
        return RepositoryFactory.orders().findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }
}
