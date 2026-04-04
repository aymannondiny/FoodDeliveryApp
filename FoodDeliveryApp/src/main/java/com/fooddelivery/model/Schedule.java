package com.fooddelivery.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a restaurant's weekly opening schedule.
 * Times are stored as "HH:mm" strings for simple JSON serialisation.
 */
public class Schedule {
    private String openTime;         // e.g. "09:00"
    private String closeTime;        // e.g. "23:00"
    private Set<DayOfWeek> openDays; // days the restaurant is open

    public Schedule() {
        this.openDays = EnumSet.noneOf(DayOfWeek.class);
    }

    public Schedule(String openTime, String closeTime, Set<DayOfWeek> openDays) {
        this.openTime  = openTime;
        this.closeTime = closeTime;
        this.openDays  = openDays;
    }

    /** Convenience factory – open every day. */
    public static Schedule allDay(String openTime, String closeTime) {
        return new Schedule(openTime, closeTime, EnumSet.allOf(DayOfWeek.class));
    }

    /** Returns true if the restaurant should currently be open. */
    public boolean isOpenNow() {
        DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
        if (!openDays.contains(today)) return false;

        LocalTime now   = LocalTime.now();
        LocalTime open  = LocalTime.parse(openTime);
        LocalTime close = LocalTime.parse(closeTime);
        return !now.isBefore(open) && now.isBefore(close);
    }

    @Override
    public String toString() {
        return openTime + " – " + closeTime + " on " + openDays;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String        getOpenTime()              { return openTime; }
    public void          setOpenTime(String t)      { this.openTime = t; }
    public String        getCloseTime()             { return closeTime; }
    public void          setCloseTime(String t)     { this.closeTime = t; }
    public Set<DayOfWeek> getOpenDays()             { return openDays; }
    public void          setOpenDays(Set<DayOfWeek> d){ this.openDays = d; }
}
