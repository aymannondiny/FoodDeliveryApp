package com.fooddelivery.infrastructure.session;

import com.fooddelivery.application.common.CurrentUserProvider;
import com.fooddelivery.model.User;

public interface CurrentSession extends CurrentUserProvider {

    void setCurrentUser(User user);

    void clear();
}