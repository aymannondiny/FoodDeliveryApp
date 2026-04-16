package com.fooddelivery.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapRestaurant")
public class SoapRestaurant {

    @XmlElement private String id;
    @XmlElement private String name;
    @XmlElement private String cuisineType;
    @XmlElement private String description;
    @XmlElement private double rating;
    @XmlElement private int totalRatings;
    @XmlElement private boolean open;
    @XmlElement private double minOrderAmount;
    @XmlElement private double deliveryFeePerKm;
    @XmlElement private int estimatedDeliveryMinutes;
    @XmlElement private String phone;
    @XmlElement private SoapAddress address;

    public SoapRestaurant() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public double getDeliveryFeePerKm() { return deliveryFeePerKm; }
    public void setDeliveryFeePerKm(double deliveryFeePerKm) { this.deliveryFeePerKm = deliveryFeePerKm; }
    public int getEstimatedDeliveryMinutes() { return estimatedDeliveryMinutes; }
    public void setEstimatedDeliveryMinutes(int estimatedDeliveryMinutes) { this.estimatedDeliveryMinutes = estimatedDeliveryMinutes; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public SoapAddress getAddress() { return address; }
    public void setAddress(SoapAddress address) { this.address = address; }
}