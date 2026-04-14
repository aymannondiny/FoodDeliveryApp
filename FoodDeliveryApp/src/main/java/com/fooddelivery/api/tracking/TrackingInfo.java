package com.fooddelivery.api.tracking;

public class TrackingInfo {

    private final String orderId;
    private final String status;
    private final String statusMessage;
    private final String updatedAt;
    private final RiderInfo rider;

    public TrackingInfo(String orderId,
                        String status,
                        String statusMessage,
                        String updatedAt,
                        RiderInfo rider) {
        this.orderId = orderId;
        this.status = status;
        this.statusMessage = statusMessage;
        this.updatedAt = updatedAt;
        this.rider = rider;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public RiderInfo getRider() {
        return rider;
    }

    public static class RiderInfo {
        private final String name;
        private final String phone;
        private final String vehicleType;

        public RiderInfo(String name, String phone, String vehicleType) {
            this.name = name;
            this.phone = phone;
            this.vehicleType = vehicleType;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getVehicleType() {
            return vehicleType;
        }
    }
}