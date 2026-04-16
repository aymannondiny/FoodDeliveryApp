package com.fooddelivery.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapTrackingInfo")
public class SoapTrackingInfo {

    @XmlElement private String orderId;
    @XmlElement private String status;
    @XmlElement private String statusMessage;
    @XmlElement private String updatedAt;
    @XmlElement private SoapRiderInfo rider;

    public SoapTrackingInfo() {
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public SoapRiderInfo getRider() { return rider; }
    public void setRider(SoapRiderInfo rider) { this.rider = rider; }
}