package com.fooddelivery.application.rider;

import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Rider;

import java.util.List;

public class GetAvailableRidersUseCase {

    private final RiderRepository riderRepository;

    public GetAvailableRidersUseCase(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public List<Rider> execute() {
        return riderRepository.findAvailable();
    }
}