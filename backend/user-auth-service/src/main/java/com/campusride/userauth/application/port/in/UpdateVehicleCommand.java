package com.campusride.userauth.application.port.in;

public record UpdateVehicleCommand(String brand, String model, String color,
        String plateNumber, int seats) {}
