package com.campusride.userauth.infrastructure.web.dto;

public record ProfileResponse(String firstName, String lastName, String phone,
                              String university, String photoUrl, String bio) {}
