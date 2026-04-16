package com.fooddelivery.api.common;

public interface Mapper<S, T> {
    T map(S source);
}