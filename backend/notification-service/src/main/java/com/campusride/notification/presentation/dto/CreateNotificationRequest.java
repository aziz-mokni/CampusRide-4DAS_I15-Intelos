package com.campusride.notification.presentation.dto;

import com.campusride.notification.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(
        @NotBlank String userId,
        @NotNull NotificationType type,
        @NotBlank @Size(max = 2000) String message) {}
