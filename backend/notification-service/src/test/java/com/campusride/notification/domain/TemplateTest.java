package com.campusride.notification.domain;

import com.campusride.notification.domain.model.Template;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateTest {

    @Test
    void replacesVariables() {
        Template t = new Template("BOOKING_CONFIRMED", "Sujet", "Reservation {{bookingId}} confirmee");
        assertEquals("Reservation b-101 confirmee", t.renderBody(Map.of("bookingId", "b-101")));
    }

    @Test
    void removesUnknownVariables() {
        Template t = new Template("X", "Sujet", "Bonjour{{firstName}}!");
        assertEquals("Bonjour!", t.renderBody(Map.of()));
    }
}
