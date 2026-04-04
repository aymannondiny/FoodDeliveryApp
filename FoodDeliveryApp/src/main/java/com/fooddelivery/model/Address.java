package com.fooddelivery.model;

/**
 * Represents a physical delivery or restaurant address.
 * Stores area for proximity-based restaurant discovery.
 */
public class Address {
    private String street;
    private String area;       // Used for nearby restaurant lookup
    private String city;
    private String zipCode;
    private double latitude;
    private double longitude;

    public Address() {}

    public Address(String street, String area, String city, String zipCode) {
        this.street = street;
        this.area = area;
        this.city = city;
        this.zipCode = zipCode;
    }

    public Address(String street, String area, String city, String zipCode,
                   double latitude, double longitude) {
        this(street, area, city, zipCode);
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    /** Calculates approximate distance (km) to another address using Haversine formula. */
    public double distanceTo(Address other) {
        if (this.latitude == 0 || other.latitude == 0) {
            // Fall back to area equality if coordinates are missing
            return this.area.equalsIgnoreCase(other.area) ? 0.0 : Double.MAX_VALUE;
        }
        final int R = 6371;
        double latDiff = Math.toRadians(other.latitude - this.latitude);
        double lngDiff = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                 + Math.cos(Math.toRadians(this.latitude))
                   * Math.cos(Math.toRadians(other.latitude))
                   * Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String toString() {
        return street + ", " + area + ", " + city + (zipCode != null ? " - " + zipCode : "");
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String getStreet()             { return street; }
    public void   setStreet(String s)     { this.street = s; }
    public String getArea()               { return area; }
    public void   setArea(String a)       { this.area = a; }
    public String getCity()               { return city; }
    public void   setCity(String c)       { this.city = c; }
    public String getZipCode()            { return zipCode; }
    public void   setZipCode(String z)    { this.zipCode = z; }
    public double getLatitude()           { return latitude; }
    public void   setLatitude(double lat) { this.latitude = lat; }
    public double getLongitude()          { return longitude; }
    public void   setLongitude(double lng){ this.longitude = lng; }
}
