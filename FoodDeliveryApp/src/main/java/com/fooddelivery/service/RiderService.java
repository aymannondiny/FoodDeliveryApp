package com.fooddelivery.service;

import com.fooddelivery.model.Rider;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.util.AppUtils;

import java.util.List;
import java.util.Optional;

/**
 * Manages rider registration, availability, and assignment.
 */
public class RiderService {

    private static RiderService instance;

    private RiderService() {}

    public static synchronized RiderService getInstance() {
        if (instance == null) instance = new RiderService();
        return instance;
    }

    public Rider register(String userId, String name, String phone, String vehicleType) {
        Rider rider = new Rider(AppUtils.generateId("RDR"), userId, name, phone, vehicleType);
        RepositoryFactory.riders().save(rider.getId(), rider);
        return rider;
    }

    public List<Rider> getAvailableRiders() {
        return RepositoryFactory.riders().findAvailable();
    }

    public List<Rider> getAllRiders() {
        return RepositoryFactory.riders().findAll();
    }

    public Optional<Rider> findById(String id) {
        return RepositoryFactory.riders().findById(id);
    }

    public void setAvailability(String riderId, boolean available) {
        RepositoryFactory.riders().findById(riderId).ifPresent(r -> {
            r.setAvailable(available);
            RepositoryFactory.riders().save(riderId, r);
        });
    }
}