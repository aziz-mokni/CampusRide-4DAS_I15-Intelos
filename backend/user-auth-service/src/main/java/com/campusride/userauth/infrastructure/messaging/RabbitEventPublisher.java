package com.campusride.userauth.infrastructure.messaging;

import com.campusride.userauth.application.port.out.EventPublisherPort;
import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class RabbitEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUserRegistered(User user) {
        send("user.registered", user.getId());
    }

    @Override
    public void publishUserBlocked(User user) {
        send("user.blocked", user.getId());
    }

    private void send(String type, String userId) {
        EventEnvelope event = new EventEnvelope(UUID.randomUUID().toString(), type,
                Instant.now().toString(), Map.of("userId", userId));
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, type, event);
            log.info("[EVENT] {} publié eventId={} userId={}", type, event.eventId(), userId);
        } catch (AmqpException e) {
            // Limite connue : si RabbitMQ est arrêté, l'événement est perdu (pas d'outbox pour l'instant).
            log.error("[EVENT] échec de publication de {} pour userId={} : {}", type, userId, e.getMessage());
        }
    }
}
