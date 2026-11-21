package com.campusride.userauth.application.port.in;

public record CreateUserCommand(String keycloakId, String email, String firstName,
        String lastName, String phone, String university, String photoUrl, String bio) {}
