package com.campusride.userauth.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class VehicleEntity {
    @Id
    private String id;
    private String brand;
    private String model;
    private String color;
    @Column(nullable = false, unique = true)
    private String plateNumber;
    private int seats;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    protected VehicleEntity() {}

    public VehicleEntity(String id) { this.id = id; }

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public void setBrand(String v) { this.brand = v; }
    public String getModel() { return model; }
    public void setModel(String v) { this.model = v; }
    public String getColor() { return color; }
    public void setColor(String v) { this.color = v; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String v) { this.plateNumber = v; }
    public int getSeats() { return seats; }
    public void setSeats(int v) { this.seats = v; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
}