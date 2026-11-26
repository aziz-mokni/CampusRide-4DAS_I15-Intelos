package com.campusride.userauth.infrastructure.messaging;

import com.campusride.userauth.domain.model.Profile;
import com.campusride.userauth.domain.model.Role;
import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.domain.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RabbitEventPublisherTest {

    private final RabbitTemplate rabbit = mock(RabbitTemplate.class);
    private final RabbitEventPublisher publisher = new RabbitEventPublisher(rabbit);
    private final User user = new User("u-1", "kc-1", "nour@esprit.tn", Role.USER,
            UserStatus.ACTIVE, Instant.now(),
            new Profile("p-1", "Nour", "Nasfi", null, null, null, null));

    @Test
    void publishesUserRegistered() {
        publisher.publishUserRegistered(user);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(rabbit).convertAndSend(eq("covoiturage.events"), eq("user.registered"), captor.capture());
        EventEnvelope event = (EventEnvelope) captor.getValue();
        assertEquals("user.registered", event.type());
        assertEquals("u-1", event.payload().get("userId"));
        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void publishesUserBlocked() {
        publisher.publishUserBlocked(user);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(rabbit).convertAndSend(eq("covoiturage.events"), eq("user.blocked"), captor.capture());
        assertEquals("user.blocked", ((EventEnvelope) captor.getValue()).type());
    }

    @Test
    void brokerDown_doesNotBreakTheCaller() {
        doThrow(new AmqpConnectException(new RuntimeException("broker arrêté")))
                .when(rabbit).convertAndSend(anyString(), anyString(), any(Object.class));

        assertDoesNotThrow(() -> publisher.publishUserRegistered(user));
    }
}
