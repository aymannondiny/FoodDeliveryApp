package com.fooddelivery.application.rider;

import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Rider;

public class SetRiderAvailabilityUseCase {

    private final RiderRepository riderRepository;

    public SetRiderAvailabilityUseCase(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public Rider execute(String riderId, boolean available) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found: " + riderId));

        if (available && rider.getCurrentOrderId() != null) {
            throw new IllegalStateException("Cannot mark rider available while handling an active order.");
        }

        rider.setAvailable(available);
        riderRepository.save(riderId, rider);
        return rider;
    }
}