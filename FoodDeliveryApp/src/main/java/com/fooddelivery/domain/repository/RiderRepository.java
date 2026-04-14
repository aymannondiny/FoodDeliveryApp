package com.fooddelivery.domain.repository;

import com.fooddelivery.model.Rider;

import java.util.List;

public interface RiderRepository extends DataRepository<Rider> {
    List<Rider> findAvailable();
}