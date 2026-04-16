package com.fooddelivery.util;

import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.infrastructure.bootstrap.AppSeeder;

/**
 * Legacy facade kept for backward compatibility.
 * New code should prefer AppSeeder directly.
 */
public final class DataSeeder {

    private DataSeeder() {
    }

    public static void seed() {
        new AppSeeder(AppContext.create()).seedIfNeeded();
    }
}