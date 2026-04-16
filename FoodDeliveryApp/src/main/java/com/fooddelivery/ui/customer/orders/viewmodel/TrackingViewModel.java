package com.fooddelivery.ui.customer.orders.viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackingViewModel {

    private final String orderId;
    private final List<TrackingStepViewModel> steps;
    private final String riderText;
    private final String paymentText;
    private final boolean canAdvanceDemo;

    public TrackingViewModel(String orderId,
                             List<TrackingStepViewModel> steps,
                             String riderText,
                             String paymentText,
                             boolean canAdvanceDemo) {
        this.orderId = orderId;
        this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
        this.riderText = riderText;
        this.paymentText = paymentText;
        this.canAdvanceDemo = canAdvanceDemo;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<TrackingStepViewModel> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public String getRiderText() {
        return riderText;
    }

    public String getPaymentText() {
        return paymentText;
    }

    public boolean canAdvanceDemo() {
        return canAdvanceDemo;
    }
}