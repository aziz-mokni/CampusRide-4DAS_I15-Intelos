package com.campusride.userauth.infrastructure.persistence;

import com.campusride.userauth.domain.model.Profile;
import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.domain.model.Vehicle;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {}

    public static UserEntity toEntity(User user) {
        UserEntity e = new UserEntity(user.getId());
        e.setKeycloakId(user.getKeycloakId());
        e.setEmail(user.getEmail());
        e.setRole(user.getRole());
        e.setStatus(user.getStatus());
        e.setCreatedAt(user.getCreatedAt());

        Profile p = user.getProfile();
        ProfileEntity pe = new ProfileEntity(p.getId());
        pe.setFirstName(p.getFirstName());
        pe.setLastName(p.getLastName());
        pe.setPhone(p.getPhone());
        pe.setUniversity(p.getUniversity());
        pe.setPhotoUrl(p.getPhotoUrl());
        pe.setBio(p.getBio());
        e.setProfile(pe);

        for (Vehicle v : user.getVehicles()) {
            VehicleEntity ve = new VehicleEntity(v.getId());
            ve.setBrand(v.getBrand());
            ve.setModel(v.getModel());
            ve.setColor(v.getColor());
            ve.setPlateNumber(v.getPlateNumber());
            ve.setSeats(v.getSeats());
            ve.setUser(e);
            e.getVehicles().add(ve);
        }
        return e;
    }

    public static User toDomain(UserEntity e) {
        ProfileEntity pe = e.getProfile();
        Profile profile = new Profile(pe.getId(), pe.getFirstName(), pe.getLastName(),
                pe.getPhone(), pe.getUniversity(), pe.getPhotoUrl(), pe.getBio());
        User user = new User(e.getId(), e.getKeycloakId(), e.getEmail(), e.getRole(),
                e.getStatus(), e.getCreatedAt(), profile);
        for (VehicleEntity ve : e.getVehicles()) {
            user.addVehicle(new Vehicle(ve.getId(), ve.getBrand(), ve.getModel(),
                    ve.getColor(), ve.getPlateNumber(), ve.getSeats()));
        }
        return user;
    }
}