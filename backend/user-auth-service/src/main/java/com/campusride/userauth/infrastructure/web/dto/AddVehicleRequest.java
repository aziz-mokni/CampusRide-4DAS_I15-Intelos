package com.campusride.userauth.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddVehicleRequest(
        @NotBlank String brand,
        @NotBlank String model,
        String color,
        @NotBlank String plateNumber,
        @Min(1) int seats) {}
