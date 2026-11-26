package com.campusride.notification.domain.exception;

/** Le service Utilisateurs est indisponible ou a repondu en erreur (autre que 404). */
public class ContactLookupException extends RuntimeException {
    public ContactLookupException(String message, Throwable cause) { super(message, cause); }
}
