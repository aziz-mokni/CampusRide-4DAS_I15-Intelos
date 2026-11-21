package com.campusride.userauth.infrastructure.web;

import com.campusride.userauth.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE = "https://campusride.tn/problems/";

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail notFound(UserNotFoundException e, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, "user-not-found", e.getMessage(), req);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail emailUsed(EmailAlreadyUsedException e, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "email-already-used", e.getMessage(), req);
    }

    @ExceptionHandler(PlateNumberAlreadyUsedException.class)
    public ProblemDetail plateUsed(PlateNumberAlreadyUsedException e, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "plate-already-used", e.getMessage(), req);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ProblemDetail blocked(UserBlockedException e, HttpServletRequest req) {
        return problem(HttpStatus.FORBIDDEN, "user-blocked", e.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail denied(AccessDeniedException e, HttpServletRequest req) {
        return problem(HttpStatus.FORBIDDEN, "access-denied", e.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalid(MethodArgumentNotValidException e, HttpServletRequest req) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return problem(HttpStatus.BAD_REQUEST, "validation-error", detail, req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail badRequest(IllegalArgumentException e, HttpServletRequest req) {
        return problem(HttpStatus.BAD_REQUEST, "bad-request", e.getMessage(), req);
    }

    private ProblemDetail problem(HttpStatus status, String slug, String detail,
                                  HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setType(URI.create(BASE + slug));
        pd.setTitle(status.getReasonPhrase());
        pd.setInstance(URI.create(req.getRequestURI()));
        return pd;
    }
}
