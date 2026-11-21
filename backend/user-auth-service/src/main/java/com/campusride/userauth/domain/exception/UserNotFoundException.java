package com.campusride.userauth.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) { super("Utilisateur introuvable : " + id); }
}
