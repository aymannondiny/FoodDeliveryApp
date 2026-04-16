package com.fooddelivery.application.rider;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.model.Rider;

public class RegisterRiderUseCase {

    private final RiderRepository riderRepository;
    private final IdGenerator idGenerator;

    public RegisterRiderUseCase(RiderRepository riderRepository,
                                IdGenerator idGenerator) {
        this.riderRepository = riderRepository;
        this.idGenerator = idGenerator;
    }

    public Rider execute(String userId, String name, String phone, String vehicleType) {
        Rider rider = new Rider(
                idGenerator.nextId("RDR"),
                userId,
                name,
                phone,
                vehicleType
        );

        riderRepository.save(rider.getId(), rider);
        return rider;
    }
}