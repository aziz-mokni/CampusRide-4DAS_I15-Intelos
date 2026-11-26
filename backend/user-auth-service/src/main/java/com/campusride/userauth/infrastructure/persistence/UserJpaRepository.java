package com.campusride.userauth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    boolean existsByVehiclesPlateNumber(String plateNumber);
    Optional<UserEntity> findByKeycloakId(String keycloakId);
}
