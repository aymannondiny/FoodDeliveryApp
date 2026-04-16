package com.fooddelivery.ui.customer.orders.viewmodel;

public class TrackingStepViewModel {

    private final String label;
    private final boolean done;
    private final boolean active;
    private final String timeText;

    public TrackingStepViewModel(String label,
                                 boolean done,
                                 boolean active,
                                 String timeText) {
        this.label = label;
        this.done = done;
        this.active = active;
        this.timeText = timeText;
    }

    public String getLabel() {
        return label;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isActive() {
        return active;
    }

    public String getTimeText() {
        return timeText;
    }
}