package com.campusride.userauth.infrastructure.messaging;

import java.util.Map;

/** Format commun des événements : eventId, type, occurredAt, payload (voir dossier technique, section 6). */
public record EventEnvelope(String eventId, String type, String occurredAt,
                            Map<String, Object> payload) {}
