package com.campusride.userauth.application.port.in;

public record AddVehicleCommand(String brand, String model, String color,
        String plateNumber, int seats) {}
