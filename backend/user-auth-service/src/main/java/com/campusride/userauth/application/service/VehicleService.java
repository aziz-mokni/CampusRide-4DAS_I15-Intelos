package com.campusride.userauth.application.service;

import com.campusride.userauth.application.port.in.*;
import com.campusride.userauth.application.port.out.UserRepositoryPort;
import com.campusride.userauth.domain.exception.*;
import com.campusride.userauth.domain.model.*;
import java.util.List;
import java.util.UUID;

public class VehicleService implements ManageVehicleUseCase {

    private final UserRepositoryPort users;

    public VehicleService(UserRepositoryPort users) {
        this.users = users;
    }

    private User load(String userId) {
        return users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public Vehicle addVehicle(String userId, AddVehicleCommand c) {
        User user = load(userId);
        if (!user.isActive()) {
            throw new UserBlockedException(userId);
        }
        if (users.existsByPlateNumber(c.plateNumber())) {
            throw new PlateNumberAlreadyUsedException(c.plateNumber());
        }
        Vehicle vehicle = new Vehicle(UUID.randomUUID().toString(), c.brand(), c.model(),
                c.color(), c.plateNumber(), c.seats());
        user.addVehicle(vehicle);
        users.save(user);
        return vehicle;
    }

    @Override
    public List<Vehicle> listVehicles(String userId) {
        return load(userId).getVehicles();
    }

    @Override
    public Vehicle updateVehicle(String userId, String vehicleId, UpdateVehicleCommand c) {
        User user = load(userId);
        if (!user.isActive()) {
            throw new UserBlockedException(userId);
        }
        if (user.findVehicle(vehicleId).isEmpty()) {
            throw new VehicleNotFoundException(vehicleId);
        }
        if (users.existsByPlateNumberExcludingVehicle(c.plateNumber(), vehicleId)) {
            throw new PlateNumberAlreadyUsedException(c.plateNumber());
        }
        Vehicle updated = new Vehicle(vehicleId, c.brand(), c.model(), c.color(),
                c.plateNumber(), c.seats());
        user.replaceVehicle(updated);
        users.save(user);
        return updated;
    }

    @Override
    public void removeVehicle(String userId, String vehicleId) {
        User user = load(userId);
        if (!user.removeVehicle(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        users.save(user);
    }
}
