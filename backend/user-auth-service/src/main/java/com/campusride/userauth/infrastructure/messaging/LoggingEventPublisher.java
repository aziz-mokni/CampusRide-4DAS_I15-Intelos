package com.campusride.userauth.infrastructure.messaging;

import com.campusride.userauth.application.port.out.EventPublisherPort;
import com.campusride.userauth.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publishUserRegistered(User user) {
        log.info("[EVENT] user.registered userId={}", user.getId());
    }

    @Override
    public void publishUserBlocked(User user) {
        log.info("[EVENT] user.blocked userId={}", user.getId());
    }
}