package com.campusride.notification.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Le createur (utilisateur connecte) est ajoute automatiquement aux participants. */
public record CreateConversationRequest(
        @NotBlank String tripId,
        @NotEmpty List<String> participantIds) {}
