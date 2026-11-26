package com.campusride.reputation.domain.exception;

/** Mapped to HTTP 403 by the web layer. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
