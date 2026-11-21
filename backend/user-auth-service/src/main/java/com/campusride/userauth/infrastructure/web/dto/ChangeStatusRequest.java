package com.campusride.userauth.infrastructure.web.dto;

import com.campusride.userauth.domain.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull UserStatus status) {}
