package com.campusride.userauth.infrastructure.web;

import com.campusride.userauth.domain.model.Profile;
import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.domain.model.Vehicle;
import com.campusride.userauth.infrastructure.web.dto.*;

public final class WebMapper {

    private WebMapper() {}

    public static UserResponse toResponse(User u) {
        Profile p = u.getProfile();
        return new UserResponse(u.getId(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getCreatedAt(), u.isDriver(),
                new ProfileResponse(p.getFirstName(), p.getLastName(), p.getPhone(),
                        p.getUniversity(), p.getPhotoUrl(), p.getBio()));
    }

    public static VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(v.getId(), v.getBrand(), v.getModel(), v.getColor(),
                v.getPlateNumber(), v.getSeats());
    }
}
