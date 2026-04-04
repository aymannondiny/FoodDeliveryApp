package com.fooddelivery.model;

/**
 * Represents a delivery rider on the platform.
 */
public class Rider {
    private String  id;
    private String  userId;
    private String  name;
    private String  phone;
    private boolean available;
    private String  currentArea;
    private String  vehicleType;   // "Bike", "Bicycle", "Car"
    private double  rating;
    private int     totalDeliveries;
    private String  currentOrderId; // null when free

    public Rider() { this.available = true; }

    public Rider(String id, String userId, String name, String phone, String vehicleType) {
        this();
        this.id          = id;
        this.userId      = userId;
        this.name        = name;
        this.phone       = phone;
        this.vehicleType = vehicleType;
    }

    public boolean isFree() { return available && currentOrderId == null; }

    @Override
    public String toString() {
        return String.format("%s (%s) – %s", name, vehicleType, available ? "Available" : "Busy");
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String  getId()                         { return id; }
    public void    setId(String id)                { this.id = id; }
    public String  getUserId()                     { return userId; }
    public void    setUserId(String uid)           { this.userId = uid; }
    public String  getName()                       { return name; }
    public void    setName(String n)               { this.name = n; }
    public String  getPhone()                      { return phone; }
    public void    setPhone(String p)              { this.phone = p; }
    public boolean isAvailable()                   { return available; }
    public void    setAvailable(boolean a)         { this.available = a; }
    public String  getCurrentArea()                { return currentArea; }
    public void    setCurrentArea(String a)        { this.currentArea = a; }
    public String  getVehicleType()                { return vehicleType; }
    public void    setVehicleType(String v)        { this.vehicleType = v; }
    public double  getRating()                     { return rating; }
    public void    setRating(double r)             { this.rating = r; }
    public int     getTotalDeliveries()            { return totalDeliveries; }
    public void    setTotalDeliveries(int t)       { this.totalDeliveries = t; }
    public String  getCurrentOrderId()             { return currentOrderId; }
    public void    setCurrentOrderId(String oid)   { this.currentOrderId = oid; }
}
