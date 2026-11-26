package com.campusride.reputation.domain.exception;

/** Mapped to HTTP 404 by the web layer. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
