package com.fooddelivery.application.rider;

import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Rider;

import java.util.Optional;

public class FindRiderByUserIdUseCase {

    private final RiderRepository riderRepository;

    public FindRiderByUserIdUseCase(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public Optional<Rider> execute(String userId) {
        return riderRepository.findAll().stream()
                .filter(rider -> userId.equals(rider.getUserId()))
                .findFirst();
    }
}