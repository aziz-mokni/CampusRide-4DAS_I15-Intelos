package com.campusride.notification.domain;

import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.model.Message;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private Conversation conversation() {
        return Conversation.builder()
                .tripId("t-42")
                .participantIds(new ArrayList<>(List.of("driver-1", "passenger-1")))
                .build();
    }

    @Test
    void participantCanSendMessage() {
        Conversation c = conversation();
        Message m = c.addMessage("passenger-1", "Bonjour");
        assertEquals("Bonjour", m.getText());
        assertEquals(1, c.getMessages().size());
    }

    @Test
    void nonParticipantCannotSendMessage() {
        Conversation c = conversation();
        assertThrows(IllegalStateException.class, () -> c.addMessage("stranger", "Salut"));
        assertTrue(c.getMessages().isEmpty());
    }

    @Test
    void blankMessageIsRejected() {
        Conversation c = conversation();
        assertThrows(IllegalArgumentException.class, () -> c.addMessage("driver-1", "  "));
    }
}
