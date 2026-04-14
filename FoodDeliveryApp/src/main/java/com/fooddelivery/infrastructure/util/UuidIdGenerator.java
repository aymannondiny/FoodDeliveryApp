package com.fooddelivery.infrastructure.util;

import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.util.AppUtils;

public class UuidIdGenerator implements IdGenerator {

    @Override
    public String nextId(String prefix) {
        return AppUtils.generateId(prefix);
    }
}