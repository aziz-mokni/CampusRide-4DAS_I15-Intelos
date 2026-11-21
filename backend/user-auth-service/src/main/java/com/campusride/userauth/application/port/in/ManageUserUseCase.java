package com.campusride.userauth.application.port.in;

import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.domain.model.UserStatus;

public interface ManageUserUseCase {
    User create(CreateUserCommand command);
    User getById(String id);
    User updateProfile(String id, UpdateProfileCommand command);
    User changeStatus(String id, UserStatus status);
}
