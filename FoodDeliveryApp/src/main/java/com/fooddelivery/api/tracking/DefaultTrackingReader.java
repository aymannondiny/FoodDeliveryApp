package com.fooddelivery.api.tracking;

import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.application.rider.FindRiderByIdUseCase;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Rider;

import java.util.Optional;

public class DefaultTrackingReader implements TrackingReader {

    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final FindRiderByIdUseCase findRiderByIdUseCase;

    public DefaultTrackingReader(GetOrderByIdUseCase getOrderByIdUseCase,
                                 FindRiderByIdUseCase findRiderByIdUseCase) {
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.findRiderByIdUseCase = findRiderByIdUseCase;
    }

    @Override
    public Optional<TrackingInfo> findByOrderId(String orderId) {
        Optional<Order> orderOpt = getOrderByIdUseCase.execute(orderId);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        TrackingInfo.RiderInfo riderInfo = null;

        if (order.getRiderId() != null) {
            Rider rider = findRiderByIdUseCase.execute(order.getRiderId()).orElse(null);
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