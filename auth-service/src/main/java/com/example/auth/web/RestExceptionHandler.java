package com.example.auth.web;

import com.example.auth.web.error.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(f -> f.getField(), f -> f.getDefaultMessage(), (a,b)->a));
        return body(HttpStatus.BAD_REQUEST, "validation_error", req, Map.of("fields", fields));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, "validation_error", req, Map.of("violations", ex.getConstraintViolations().toString()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCreds(InvalidCredentialsException ex, HttpServletRequest req) {
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<?> handleInvalidRefresh(RefreshTokenInvalidException ex, HttpServletRequest req) {
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    @ExceptionHandler({ UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class, DataIntegrityViolationException.class })
    public ResponseEntity<?> handleConflict(RuntimeException ex, HttpServletRequest req) {
        String code = ex instanceof DataIntegrityViolationException ? "conflict" : ex.getMessage();
        return body(HttpStatus.CONFLICT, code, req, null);
    }

    @ExceptionHandler(IamUnavailableException.class)
    public ResponseEntity<?> handleIamDown(IamUnavailableException ex, HttpServletRequest req) {
        return body(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadReq(IllegalArgumentException ex, HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, "bad_request", req, Map.of("detail", ex.getMessage()));
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<?> handleSpringErr(ErrorResponseException ex, HttpServletRequest req) {
        return body(HttpStatus.valueOf(ex.getStatusCode().value()), "error", req, Map.of("detail", ex.getBody()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception ex, HttpServletRequest req) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", req, Map.of("detail", ex.getClass().getSimpleName()));
    }

    private ResponseEntity<Map<String,Object>> body(HttpStatus status, String code, HttpServletRequest req, Map<String,Object> extra) {
        var base = new java.util.LinkedHashMap<String,Object>();
        base.put("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        base.put("status", status.value());
        base.put("code", code);
        base.put("path", req.getRequestURI());
        if (extra != null) base.putAll(extra);
        return ResponseEntity.status(status).body(base);
    }
}
