package com.fooddelivery.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapAddress")
public class SoapAddress {

    @XmlElement
    private String street;

    @XmlElement
    private String area;

    @XmlElement
    private String city;

    @XmlElement
    private double latitude;

    @XmlElement
    private double longitude;

    public SoapAddress() {
    }

    public SoapAddress(String street, String area, String city, double latitude, double longitude) {
        this.street = street;
        this.area = area;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}