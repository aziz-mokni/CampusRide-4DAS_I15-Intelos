package com.campusride.userauth.domain.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException(String id) { super("Utilisateur bloqué : " + id); }
}
