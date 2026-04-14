package com.fooddelivery.api.tracking;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.Rider;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RiderService;

import java.util.Optional;

public class DefaultTrackingReader implements TrackingReader {

    private final OrderService orderService;
    private final RiderService riderService;

    public DefaultTrackingReader(OrderService orderService, RiderService riderService) {
        this.orderService = orderService;
        this.riderService = riderService;
    }

    @Override
    public Optional<TrackingInfo> findByOrderId(String orderId) {
        Optional<Order> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        TrackingInfo.RiderInfo riderInfo = null;

        if (order.getRiderId() != null) {
            Rider rider = riderService.findById(order.getRiderId()).orElse(null);
            if (rider != null) {
                riderInfo = new TrackingInfo.RiderInfo(
                        rider.getName(),
                        rider.getPhone(),
                        rider.getVehicleType()
                );
            }
        }

        return Optional.of(new TrackingInfo(
                order.getId(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getStatus() != null ? order.getStatus().getDescription() : null,
                order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null,
                riderInfo
        ));
    }
}