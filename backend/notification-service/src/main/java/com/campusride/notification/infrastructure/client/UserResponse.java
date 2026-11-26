package com.campusride.notification.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Reponse de GET /api/v1/users/{id} (seuls les champs utiles sont lus). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponse(String id, String email, String firstName, String lastName) {}
