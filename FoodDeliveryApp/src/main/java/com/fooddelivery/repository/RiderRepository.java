package com.fooddelivery.repository;

import com.fooddelivery.model.Rider;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class RiderRepository extends FileRepository<Rider>
        implements com.fooddelivery.domain.repository.RiderRepository {

    private static RiderRepository instance;

    private RiderRepository() {
        super("data/riders.json", new TypeToken<Map<String, Rider>>() {}.getType());
    }

    public static synchronized RiderRepository getInstance() {
        if (instance == null) {
            instance = new RiderRepository();
        }
        return instance;
    }

    @Override
    public List<Rider> findAvailable() {
        return findWhere(Rider::isFree);
    }
}