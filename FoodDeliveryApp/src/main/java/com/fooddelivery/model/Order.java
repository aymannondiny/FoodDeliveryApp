package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a customer's food order from creation to delivery.
 * Maintains a full status-change history for tracking.
 */
public class Order {

    public enum PaymentMethod { CASH_ON_DELIVERY, BKASH, NAGAD, CARD }

    private String        id;
    private String        customerId;
    private String        restaurantId;
    private String        restaurantName;    // Snapshot
    private List<OrderItem> items;
    private OrderStatus   status;
    private Address       deliveryAddress;
    private String        riderId;
    private PaymentMethod paymentMethod;
    private double        subtotal;
    private double        deliveryFee;
    private double        discount;
    private double        totalAmount;
    private String        couponCode;
    private String        specialNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<OrderStatus, LocalDateTime> statusHistory; // ordered by insertion

    public Order() {
        this.items         = new ArrayList<>();
        this.status        = OrderStatus.PLACED;
        this.statusHistory = new LinkedHashMap<>();
        this.createdAt     = LocalDateTime.now();
        this.updatedAt     = LocalDateTime.now();
        this.statusHistory.put(OrderStatus.PLACED, this.createdAt);
    }

    public Order(String id, String customerId, String restaurantId,
                 String restaurantName, Address deliveryAddress,
                 PaymentMethod paymentMethod) {
        this();
        this.id             = id;
        this.customerId     = customerId;
        this.restaurantId   = restaurantId;
        this.restaurantName = restaurantName;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod  = paymentMethod;
    }

    /** Recalculate totals from items + fee - discount. */
    public void recalculateTotals() {
        this.subtotal    = items.stream().mapToDouble(OrderItem::getLineTotal).sum();
        this.totalAmount = subtotal + deliveryFee - discount;
    }

    /** Progress the order to the next status and record the timestamp. */
    public void advanceStatus(OrderStatus newStatus) {
        this.status    = newStatus;
        this.updatedAt = LocalDateTime.now();
        this.statusHistory.put(newStatus, this.updatedAt);
    }

    public boolean isCancellable() {
        return status == OrderStatus.PLACED || status == OrderStatus.CONFIRMED;
    }

    @Override
    public String toString() {
        return String.format("Order#%s from %s – %s (%.2f BDT)",
                             id, restaurantName, status, totalAmount);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String        getId()                              { return id; }
    public void          setId(String id)                     { this.id = id; }
    public String        getCustomerId()                      { return customerId; }
    public void          setCustomerId(String cid)            { this.customerId = cid; }
    public String        getRestaurantId()                    { return restaurantId; }
    public void          setRestaurantId(String rid)          { this.restaurantId = rid; }
    public String        getRestaurantName()                  { return restaurantName; }
    public void          setRestaurantName(String n)          { this.restaurantName = n; }
    public List<OrderItem> getItems()                         { return items; }
    public void          setItems(List<OrderItem> items)      { this.items = items; }
    public OrderStatus   getStatus()                          { return status; }
    public void          setStatus(OrderStatus s)             { this.status = s; }
    public Address       getDeliveryAddress()                 { return deliveryAddress; }
    public void          setDeliveryAddress(Address a)        { this.deliveryAddress = a; }
    public String        getRiderId()                         { return riderId; }
    public void          setRiderId(String rid)               { this.riderId = rid; }
    public PaymentMethod getPaymentMethod()                   { return paymentMethod; }
    public void          setPaymentMethod(PaymentMethod pm)   { this.paymentMethod = pm; }
    public double        getSubtotal()                        { return subtotal; }
    public void          setSubtotal(double s)                { this.subtotal = s; }
    public double        getDeliveryFee()                     { return deliveryFee; }
    public void          setDeliveryFee(double f)             { this.deliveryFee = f; }
    public double        getDiscount()                        { return discount; }
    public void          setDiscount(double d)                { this.discount = d; }
    public double        getTotalAmount()                     { return totalAmount; }
    public void          setTotalAmount(double t)             { this.totalAmount = t; }
    public String        getCouponCode()                      { return couponCode; }
    public void          setCouponCode(String c)              { this.couponCode = c; }
    public String        getSpecialNote()                     { return specialNote; }
    public void          setSpecialNote(String n)             { this.specialNote = n; }
    public LocalDateTime getCreatedAt()                       { return createdAt; }
    public void          setCreatedAt(LocalDateTime dt)       { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()                       { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime dt)       { this.updatedAt = dt; }
    public Map<OrderStatus, LocalDateTime> getStatusHistory() { return statusHistory; }
    public void          setStatusHistory(Map<OrderStatus, LocalDateTime> h){ this.statusHistory = h; }
}
