package com.campusride.userauth.application.port.out;

import com.campusride.userauth.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByKeycloakId(String keycloakId);
    List<User> findAll(int page, int size);
    long count();
    boolean existsByEmail(String email);
    boolean existsByPlateNumber(String plateNumber);
    boolean existsByPlateNumberExcludingVehicle(String plateNumber, String vehicleId);
}
