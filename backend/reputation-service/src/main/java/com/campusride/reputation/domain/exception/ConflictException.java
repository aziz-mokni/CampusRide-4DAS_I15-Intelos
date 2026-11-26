package com.campusride.reputation.domain.exception;

/** Mapped to HTTP 409 by the web layer. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
