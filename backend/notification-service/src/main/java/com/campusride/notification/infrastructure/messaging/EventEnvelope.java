package com.campusride.notification.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/** Enveloppe commune des evenements : { eventId, type, occurredAt, payload }. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EventEnvelope(String eventId, String type, String occurredAt, Map<String, Object> payload) {}
