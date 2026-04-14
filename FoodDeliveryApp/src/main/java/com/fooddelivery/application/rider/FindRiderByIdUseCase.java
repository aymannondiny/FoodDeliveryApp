package com.fooddelivery.application.rider;

import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Rider;

import java.util.Optional;

public class FindRiderByIdUseCase {

    private final RiderRepository riderRepository;

    public FindRiderByIdUseCase(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public Optional<Rider> execute(String riderId) {
        return riderRepository.findById(riderId);
    }
}