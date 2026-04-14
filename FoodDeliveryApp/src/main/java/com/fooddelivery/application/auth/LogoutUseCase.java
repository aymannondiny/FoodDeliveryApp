package com.fooddelivery.application.auth;

import com.fooddelivery.infrastructure.session.CurrentSession;

public class LogoutUseCase {

    private final CurrentSession currentSession;

    public LogoutUseCase(CurrentSession currentSession) {
        this.currentSession = currentSession;
    }

    public void execute() {
        currentSession.clear();
    }
}