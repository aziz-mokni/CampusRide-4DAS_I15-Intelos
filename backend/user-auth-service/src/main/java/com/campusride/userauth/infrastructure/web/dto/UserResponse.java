package com.campusride.userauth.infrastructure.web.dto;

import com.campusride.userauth.domain.model.Role;
import com.campusride.userauth.domain.model.UserStatus;
import java.time.Instant;

public record UserResponse(String id, String email, String firstName, String lastName,
                           Role role, UserStatus status, Instant createdAt,
                           boolean driver, ProfileResponse profile) {}