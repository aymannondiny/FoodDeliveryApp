package com.fooddelivery.infrastructure.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.domain.service.RiderAssigner;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Rider;

import java.util.List;

public class RepositoryRiderAssigner implements RiderAssigner {

    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;

    public RepositoryRiderAssigner(RiderRepository riderRepository,
                                   OrderRepository orderRepository) {
        this.riderRepository = riderRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void assignTo(Order order) {
        List<Rider> available = riderRepository.findAvailable();
        if (available.isEmpty()) {
            return;
        }

        Rider rider = available.get(0);
        rider.setCurrentOrderId(order.getId());
        rider.setAvailable(false);
        riderRepository.save(rider.getId(), rider);

        order.setRiderId(rider.getId());
        orderRepository.save(order.getId(), order);
    }
}