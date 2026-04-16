package com.fooddelivery.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapMenuItem")
public class SoapMenuItem {

    @XmlElement private String id;
    @XmlElement private String name;
    @XmlElement private String description;
    @XmlElement private String category;
    @XmlElement private double price;
    @XmlElement private boolean available;
    @XmlElement private int quantity;

    @XmlElementWrapper(name = "addons")
    @XmlElement(name = "addon")
    private List<SoapMenuAddon> addons = new ArrayList<>();

    public SoapMenuItem() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public List<SoapMenuAddon> getAddons() { return addons; }
    public void setAddons(List<SoapMenuAddon> addons) { this.addons = addons; }
}