package com.fooddelivery.ui.restaurant.riderdashboard.viewmodel;

public class RiderStatsViewModel {

    private final String totalDeliveriesText;
    private final String totalEarningsText;
    private final String vehicleTypeText;
    private final String statusText;

    public RiderStatsViewModel(String totalDeliveriesText,
                               String totalEarningsText,
                               String vehicleTypeText,
                               String statusText) {
        this.totalDeliveriesText = totalDeliveriesText;
        this.totalEarningsText = totalEarningsText;
        this.vehicleTypeText = vehicleTypeText;
        this.statusText = statusText;
    }

    public String getTotalDeliveriesText() {
        return totalDeliveriesText;
    }

    public String getTotalEarningsText() {
        return totalEarningsText;
    }

    public String getVehicleTypeText() {
        return vehicleTypeText;
    }

    public String getStatusText() {
        return statusText;
    }
}