package com.fooddelivery.domain.repository;

import java.util.List;
import java.util.Optional;


 // Generic repository contract for aggregate persistence.

public interface DataRepository<T> {

    void save(String id, T entity);

    Optional<T> findById(String id);

    List<T> findAll();

    void delete(String id);

    boolean exists(String id);

    int count();
}