package com.campusride.userauth.domain.exception;

public class PlateNumberAlreadyUsedException extends RuntimeException {
    public PlateNumberAlreadyUsedException(String plate) { super("Plaque déjà enregistrée : " + plate); }
}
