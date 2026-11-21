package com.campusride.userauth.domain.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) { super("E-mail déjà utilisé : " + email); }
}
