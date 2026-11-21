package com.campusride.userauth.application.port.in;

public record UpdateProfileCommand(String firstName, String lastName, String phone,
        String university, String photoUrl, String bio) {}
