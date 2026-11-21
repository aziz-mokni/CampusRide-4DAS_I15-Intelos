package com.campusride.userauth.application.port.in;

import com.campusride.userauth.domain.model.Vehicle;
import java.util.List;

public interface ManageVehicleUseCase {
    Vehicle addVehicle(String userId, AddVehicleCommand command);
    List<Vehicle> listVehicles(String userId);
}
