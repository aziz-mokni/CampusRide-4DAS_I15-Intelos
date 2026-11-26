package com.campusride.userauth.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class User {
    private final String id;
    private final String keycloakId;   // repris du JWT, aucun mot de passe stocké
    private final String email;
    private Role role;
    private UserStatus status;
    private final Instant createdAt;
    private Profile profile;
    private final List<Vehicle> vehicles = new ArrayList<>();

    public User(String id, String keycloakId, String email, Role role,
                UserStatus status, Instant createdAt, Profile profile) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.profile = profile;
    }

    public boolean isActive() { return status == UserStatus.ACTIVE; }

    public boolean isDriver() { return !vehicles.isEmpty(); }

    public void block() { this.status = UserStatus.BLOCKED; }

    public void activate() { this.status = UserStatus.ACTIVE; }

    public void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }

    public Optional<Vehicle> findVehicle(String vehicleId) {
        return vehicles.stream().filter(v -> v.getId().equals(vehicleId)).findFirst();
    }

    public boolean removeVehicle(String vehicleId) {
        return vehicles.removeIf(v -> v.getId().equals(vehicleId));
    }

    public void replaceVehicle(Vehicle updated) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(updated.getId())) {
                vehicles.set(i, updated);
                return;
            }
        }
    }

    public String getId() { return id; }
    public String getKeycloakId() { return keycloakId; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Profile getProfile() { return profile; }
    public List<Vehicle> getVehicles() { return Collections.unmodifiableList(vehicles); }
}
