package com.campusride.userauth.infrastructure.web.dto;

public record VehicleResponse(String id, String brand, String model, String color,
                              String plateNumber, int seats) {}
