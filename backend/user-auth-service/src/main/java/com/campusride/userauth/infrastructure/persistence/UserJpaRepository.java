package com.campusride.userauth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    boolean existsByVehiclesPlateNumber(String plateNumber);
}