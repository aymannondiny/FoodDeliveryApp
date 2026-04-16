package com.fooddelivery.application.rider;

import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Rider;

import java.util.List;

public class GetAllRidersUseCase {

    private final RiderRepository riderRepository;

    public GetAllRidersUseCase(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public List<Rider> execute() {
        return riderRepository.findAll();
    }
}