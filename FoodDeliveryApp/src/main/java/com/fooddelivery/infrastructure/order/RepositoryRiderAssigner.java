package com.fooddelivery.infrastructure.order;

import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.domain.service.RiderAssigner;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Rider;

import java.util.Comparator;
import java.util.List;

/**
 * Assigns the available rider with the fewest total deliveries.
 * This provides basic load balancing across riders.
 */
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
        if (order.getRiderId() != null) {
            return;
        }

        List<Rider> available = riderRepository.findAvailable();
        if (available.isEmpty()) {
            return;
        }

        Rider bestRider = available.stream()
                .sorted(Comparator
                        .comparingInt(Rider::getTotalDeliveries)
                        .thenComparing(Rider::getId))
                .findFirst()
                .orElse(null);

        if (bestRider == null) {
            return;
        }

        bestRider.setCurrentOrderId(order.getId());
        bestRider.setAvailable(false);
        riderRepository.save(bestRider.getId(), bestRider);

        order.setRiderId(bestRider.getId());
        orderRepository.save(order.getId(), order);
    }
}