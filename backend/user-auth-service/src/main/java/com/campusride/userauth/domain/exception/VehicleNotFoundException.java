package com.campusride.userauth.domain.exception;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String id) { super("Véhicule introuvable : " + id); }
}
