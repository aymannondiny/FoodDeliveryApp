package com.fooddelivery.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapMenuAddon")
public class SoapMenuAddon {

    @XmlElement private String id;
    @XmlElement private String name;
    @XmlElement private double extraPrice;

    public SoapMenuAddon() {
    }

    public SoapMenuAddon(String id, String name, double extraPrice) {
        this.id = id;
        this.name = name;
        this.extraPrice = extraPrice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getExtraPrice() { return extraPrice; }
    public void setExtraPrice(double extraPrice) { this.extraPrice = extraPrice; }
}