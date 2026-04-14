package com.fooddelivery.model;

import java.time.LocalDate;


public class Coupon {
    private String    id;
    private String    code;               // User-entered code (e.g. "WELCOME20")
    private double    discountPercent;    // e.g. 20 for 20 %
    private double    maxDiscountAmount;  // cap in BDT (0 = no cap)
    private double    minOrderAmount;     // minimum cart value to use
    private LocalDate expiryDate;
    private boolean   active;
    private int       usageLimit;         // -1 = unlimited
    private int       usageCount;

    public Coupon() { this.active = true; this.usageLimit = -1; }

    public Coupon(String id, String code, double discountPercent,
                  double maxDiscountAmount, double minOrderAmount, LocalDate expiryDate) {
        this();
        this.id                 = id;
        this.code               = code;
        this.discountPercent    = discountPercent;
        this.maxDiscountAmount  = maxDiscountAmount;
        this.minOrderAmount     = minOrderAmount;
        this.expiryDate         = expiryDate;
    }

    public boolean isValid() {
        return active
            && !LocalDate.now().isAfter(expiryDate)
            && (usageLimit == -1 || usageCount < usageLimit);
    }

    /**
     * Calculate the discount amount for a given order subtotal.
     * Returns 0 if the coupon is invalid or minimum order not met.
     */
    public double calculateDiscount(double orderSubtotal) {
        if (!isValid() || orderSubtotal < minOrderAmount) return 0.0;
        double discount = orderSubtotal * discountPercent / 100.0;
        return (maxDiscountAmount > 0) ? Math.min(discount, maxDiscountAmount) : discount;
    }

    public void incrementUsage() { usageCount++; }

    @Override
    public String toString() {
        return String.format("%s – %.0f%% off (max %.0f BDT, min order %.0f BDT)",
                             code, discountPercent, maxDiscountAmount, minOrderAmount);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String    getId()                          { return id; }
    public void      setId(String id)                 { this.id = id; }
    public String    getCode()                        { return code; }
    public void      setCode(String c)                { this.code = c; }
    public double    getDiscountPercent()             { return discountPercent; }
    public void      setDiscountPercent(double d)     { this.discountPercent = d; }
    public double    getMaxDiscountAmount()           { return maxDiscountAmount; }
    public void      setMaxDiscountAmount(double m)   { this.maxDiscountAmount = m; }
    public double    getMinOrderAmount()              { return minOrderAmount; }
    public void      setMinOrderAmount(double m)      { this.minOrderAmount = m; }
    public LocalDate getExpiryDate()                  { return expiryDate; }
    public void      setExpiryDate(LocalDate d)       { this.expiryDate = d; }
    public boolean   isActive()                       { return active; }
    public void      setActive(boolean a)             { this.active = a; }
    public int       getUsageLimit()                  { return usageLimit; }
    public void      setUsageLimit(int l)             { this.usageLimit = l; }
    public int       getUsageCount()                  { return usageCount; }
    public void      setUsageCount(int c)             { this.usageCount = c; }
}
