package com.fooddelivery.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapOrder")
public class SoapOrder {

    @XmlElement private String id;
    @XmlElement private String restaurantName;
    @XmlElement private String status;
    @XmlElement private String statusMessage;
    @XmlElement private double subtotal;
    @XmlElement private double deliveryFee;
    @XmlElement private double discount;
    @XmlElement private double totalAmount;
    @XmlElement private String paymentMethod;
    @XmlElement private String createdAt;
    @XmlElement private String couponCode;

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<SoapOrderItem> items = new ArrayList<>();

    public SoapOrder() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public List<SoapOrderItem> getItems() { return items; }
    public void setItems(List<SoapOrderItem> items) { this.items = items; }
}