package com.fooddelivery.model;

/**
 * Represents a restaurant registered on the platform.
 */
public class Restaurant {
    private String   id;
    private String   ownerId;
    private String   name;
    private String   description;
    private String   cuisineType;       // e.g. "Bangladeshi", "Chinese", "Fast Food"
    private Address  address;
    private double   rating;            // 0.0 – 5.0
    private int      totalRatings;
    private boolean  isOpen;            // Manual open/close override
    private Schedule schedule;
    private double   deliveryFeePerKm;
    private double   minOrderAmount;
    private int      estimatedDeliveryMinutes;
    private String   phoneNumber;
    private String   logoUrl;
    private boolean  approved;          // Admin approval flag

    public Restaurant() {
        this.isOpen    = false;
        this.approved  = false;
        this.rating    = 0.0;
    }

    public Restaurant(String id, String ownerId, String name, String cuisineType, Address address) {
        this();
        this.id          = id;
        this.ownerId     = ownerId;
        this.name        = name;
        this.cuisineType = cuisineType;
        this.address     = address;
    }

    /** Computes whether the restaurant is currently accepting orders. */
    public boolean isCurrentlyOpen() {
        return approved && isOpen && (schedule == null || schedule.isOpenNow());
    }

    /** Update aggregate rating with a new score. */
    public void addRating(double score) {
        double total = rating * totalRatings + score;
        totalRatings++;
        rating = total / totalRatings;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) ★ %.1f", name, cuisineType, rating);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String   getId()                              { return id; }
    public void     setId(String id)                     { this.id = id; }
    public String   getOwnerId()                         { return ownerId; }
    public void     setOwnerId(String oid)               { this.ownerId = oid; }
    public String   getName()                            { return name; }
    public void     setName(String n)                    { this.name = n; }
    public String   getDescription()                     { return description; }
    public void     setDescription(String d)             { this.description = d; }
    public String   getCuisineType()                     { return cuisineType; }
    public void     setCuisineType(String c)             { this.cuisineType = c; }
    public Address  getAddress()                         { return address; }
    public void     setAddress(Address a)                { this.address = a; }
    public double   getRating()                          { return rating; }
    public void     setRating(double r)                  { this.rating = r; }
    public int      getTotalRatings()                    { return totalRatings; }
    public void     setTotalRatings(int t)               { this.totalRatings = t; }
    public boolean  isOpen()                             { return isOpen; }
    public void     setOpen(boolean open)                { this.isOpen = open; }
    public Schedule getSchedule()                        { return schedule; }
    public void     setSchedule(Schedule s)              { this.schedule = s; }
    public double   getDeliveryFeePerKm()                { return deliveryFeePerKm; }
    public void     setDeliveryFeePerKm(double f)        { this.deliveryFeePerKm = f; }
    public double   getMinOrderAmount()                  { return minOrderAmount; }
    public void     setMinOrderAmount(double m)          { this.minOrderAmount = m; }
    public int      getEstimatedDeliveryMinutes()        { return estimatedDeliveryMinutes; }
    public void     setEstimatedDeliveryMinutes(int m)   { this.estimatedDeliveryMinutes = m; }
    public String   getPhoneNumber()                     { return phoneNumber; }
    public void     setPhoneNumber(String p)             { this.phoneNumber = p; }
    public String   getLogoUrl()                         { return logoUrl; }
    public void     setLogoUrl(String url)               { this.logoUrl = url; }
    public boolean  isApproved()                         { return approved; }
    public void     setApproved(boolean a)               { this.approved = a; }
}
