package com.fooddelivery.service;

import com.fooddelivery.application.rider.FindRiderByIdUseCase;
import com.fooddelivery.application.rider.GetAllRidersUseCase;
import com.fooddelivery.application.rider.GetAvailableRidersUseCase;
import com.fooddelivery.application.rider.RegisterRiderUseCase;
import com.fooddelivery.application.rider.SetRiderAvailabilityUseCase;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.model.Rider;

import java.util.List;
import java.util.Optional;

/**
 * Legacy facade kept for backward compatibility during migration.
 * New code should prefer rider use cases from AppContext.
 */
@Deprecated
public class RiderService {

    private static RiderService instance;

    private final RegisterRiderUseCase registerRiderUseCase;
    private final GetAvailableRidersUseCase getAvailableRidersUseCase;
    private final GetAllRidersUseCase getAllRidersUseCase;
    private final FindRiderByIdUseCase findRiderByIdUseCase;
    private final SetRiderAvailabilityUseCase setRiderAvailabilityUseCase;

    private RiderService() {
        AppContext context = AppContext.create();
        this.registerRiderUseCase = context.registerRiderUseCase();
        this.getAvailableRidersUseCase = context.getAvailableRidersUseCase();
        this.getAllRidersUseCase = context.getAllRidersUseCase();
        this.findRiderByIdUseCase = context.findRiderByIdUseCase();
        this.setRiderAvailabilityUseCase = context.setRiderAvailabilityUseCase();
    }

    public static synchronized RiderService getInstance() {
        if (instance == null) {
            instance = new RiderService();
        }
        return instance;
    }

    public Rider register(String userId, String name, String phone, String vehicleType) {
        return registerRiderUseCase.execute(userId, name, phone, vehicleType);
    }

    public List<Rider> getAvailableRiders() {
        return getAvailableRidersUseCase.execute();
    }

    public List<Rider> getAllRiders() {
        return getAllRidersUseCase.execute();
    }

    public Optional<Rider> findById(String id) {
        return findRiderByIdUseCase.execute(id);
    }

    public void setAvailability(String riderId, boolean available) {
        setRiderAvailabilityUseCase.execute(riderId, available);
    }
}