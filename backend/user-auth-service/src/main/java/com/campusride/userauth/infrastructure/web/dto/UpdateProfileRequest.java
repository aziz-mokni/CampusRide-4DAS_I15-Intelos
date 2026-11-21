package com.campusride.userauth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        String university,
        String photoUrl,
        String bio) {}
