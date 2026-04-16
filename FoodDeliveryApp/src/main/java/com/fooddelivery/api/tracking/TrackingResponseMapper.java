package com.fooddelivery.api.tracking;

import com.fooddelivery.api.common.Mapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class TrackingResponseMapper implements Mapper<TrackingInfo, Map<String, Object>> {

    @Override
    public Map<String, Object> map(TrackingInfo info) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", info.getOrderId());
        payload.put("status", info.getStatus());
        payload.put("statusMessage", info.getStatusMessage());
        payload.put("updatedAt", info.getUpdatedAt());

        if (info.getRider() != null) {
            Map<String, Object> rider = new LinkedHashMap<>();
            rider.put("name", info.getRider().getName());
            rider.put("phone", info.getRider().getPhone());
            rider.put("vehicleType", info.getRider().getVehicleType());
            payload.put("rider", rider);
        }

        return payload;
    }
}