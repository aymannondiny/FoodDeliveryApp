package com.fooddelivery.api.tracking;

import java.util.Optional;

public interface TrackingReader {
    Optional<TrackingInfo> findByOrderId(String orderId);
}