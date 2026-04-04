package com.fooddelivery.model;

import java.time.LocalDateTime;

/**
 * Records a payment transaction linked to an order.
 */
public class Payment {

    public enum Status { PENDING, COMPLETED, FAILED, REFUNDED }

    private String        id;
    private String        orderId;
    private Order.PaymentMethod method;
    private double        amount;
    private Status        status;
    private String        transactionId;  // External payment gateway reference
    private LocalDateTime timestamp;
    private String        failureReason;

    public Payment() {
        this.status    = Status.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public Payment(String id, String orderId, Order.PaymentMethod method, double amount) {
        this();
        this.id      = id;
        this.orderId = orderId;
        this.method  = method;
        this.amount  = amount;
    }

    @Override
    public String toString() {
        return String.format("Payment#%s – %.2f BDT via %s [%s]",
                             id, amount, method, status);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String        getId()                      { return id; }
    public void          setId(String id)             { this.id = id; }
    public String        getOrderId()                 { return orderId; }
    public void          setOrderId(String oid)       { this.orderId = oid; }
    public Order.PaymentMethod getMethod()            { return method; }
    public void          setMethod(Order.PaymentMethod m){ this.method = m; }
    public double        getAmount()                  { return amount; }
    public void          setAmount(double a)          { this.amount = a; }
    public Status        getStatus()                  { return status; }
    public void          setStatus(Status s)          { this.status = s; }
    public String        getTransactionId()           { return transactionId; }
    public void          setTransactionId(String t)   { this.transactionId = t; }
    public LocalDateTime getTimestamp()               { return timestamp; }
    public void          setTimestamp(LocalDateTime t){ this.timestamp = t; }
    public String        getFailureReason()           { return failureReason; }
    public void          setFailureReason(String r)   { this.failureReason = r; }
}
