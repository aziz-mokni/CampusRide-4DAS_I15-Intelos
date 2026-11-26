package com.campusride.userauth.infrastructure.messaging;

import com.campusride.userauth.application.port.in.ManageUserUseCase;
import com.campusride.userauth.domain.exception.UserNotFoundException;
import com.campusride.userauth.domain.model.UserStatus;
import com.campusride.userauth.infrastructure.persistence.ProcessedEventEntity;
import com.campusride.userauth.infrastructure.persistence.ProcessedEventJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BlockRequestedListenerTest {

    private final ManageUserUseCase users = mock(ManageUserUseCase.class);
    private final ProcessedEventJpaRepository processed = mock(ProcessedEventJpaRepository.class);
    private final BlockRequestedListener listener = new BlockRequestedListener(users, processed);

    private EventEnvelope event(String eventId, Map<String, Object> payload) {
        return new EventEnvelope(eventId, "block.requested", "2026-11-21T10:00:00Z", payload);
    }

    @Test
    void blocksTheUser() {
        listener.onBlockRequested(event("e1", Map.of("userId", "u-1")));

        verify(users).changeStatus("u-1", UserStatus.BLOCKED);
        verify(processed).save(any(ProcessedEventEntity.class));
    }

    @Test
    void duplicateEventIsIgnored() {
        when(processed.existsById("e2")).thenReturn(true);

        listener.onBlockRequested(event("e2", Map.of("userId", "u-1")));

        verifyNoInteractions(users);
        verify(processed, never()).save(any(ProcessedEventEntity.class));
    }

    @Test
    void unknownUser_doesNotThrow() {
        when(users.changeStatus("ghost", UserStatus.BLOCKED)).thenThrow(new UserNotFoundException("ghost"));

        assertDoesNotThrow(() -> listener.onBlockRequested(event("e3", Map.of("userId", "ghost"))));
        verify(processed).save(any(ProcessedEventEntity.class));
    }

    @Test
    void payloadWithoutUserId_isIgnored() {
        listener.onBlockRequested(event("e4", Map.of("reason", "x")));

        verifyNoInteractions(users);
    }
}
