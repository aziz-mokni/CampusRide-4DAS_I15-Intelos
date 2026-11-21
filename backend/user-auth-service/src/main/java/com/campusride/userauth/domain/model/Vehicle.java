package com.campusride.userauth.domain.model;

public class Vehicle {
    private final String id;
    private final String brand;
    private final String model;
    private final String color;
    private final String plateNumber;
    private final int seats;

    public Vehicle(String id, String brand, String model, String color,
                   String plateNumber, int seats) {
        if (seats < 1) {
            throw new IllegalArgumentException("Un véhicule doit avoir au moins 1 place");
        }
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.plateNumber = plateNumber;
        this.seats = seats;
    }

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public String getPlateNumber() { return plateNumber; }
    public int getSeats() { return seats; }
}
