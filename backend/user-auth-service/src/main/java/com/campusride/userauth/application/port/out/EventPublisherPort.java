package com.campusride.userauth.application.port.out;

import com.campusride.userauth.domain.model.User;

public interface EventPublisherPort {
    void publishUserRegistered(User user);   // user.registered
    void publishUserBlocked(User user);      // user.blocked
}
