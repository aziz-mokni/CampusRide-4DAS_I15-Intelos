package com.campusride.userauth.application.port.out;

import com.campusride.userauth.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    boolean existsByEmail(String email);
    boolean existsByPlateNumber(String plateNumber);
}
