package com.campusride.userauth.application.port.in;

import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.domain.model.UserStatus;
import java.util.List;

public interface ManageUserUseCase {
    User create(CreateUserCommand command);
    User getById(String id);
    User getByKeycloakId(String keycloakId);
    List<User> list(int page, int size);
    long count();
    User updateProfile(String id, UpdateProfileCommand command);
    User changeStatus(String id, UserStatus status);
}
