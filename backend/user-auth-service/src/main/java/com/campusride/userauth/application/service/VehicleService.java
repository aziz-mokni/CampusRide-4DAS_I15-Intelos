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

    @Override
    public Vehicle addVehicle(String userId, AddVehicleCommand c) {
        User user = users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
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
        return users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId))
                .getVehicles();
    }
}
