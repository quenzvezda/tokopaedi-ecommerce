package com.example.auth.web;

import com.example.common.web.ErrorResponseBuilder;
import com.example.auth.web.error.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseBuilder errors;

    // 400 – body JSON tidak valid
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> badJson(HttpServletRequest req, HttpMessageNotReadableException ex) {
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:malformed_json", "Malformed JSON body", ex, null);
    }

    // 400 – validasi @Valid pada body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> beanValidation(HttpServletRequest req, MethodArgumentNotValidException ex) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", Objects.toString(fe.getDefaultMessage(), "")
                )).toList());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:validation", "Validation failed", ex, meta);
    }

    // 400 – validasi param/path
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolation(HttpServletRequest req, ConstraintViolationException ex) {
        Map<String, Object> meta = Map.of("errors",
                ex.getConstraintViolations().stream()
                        .map(cv -> Map.of("property", cv.getPropertyPath().toString(), "message", cv.getMessage()))
                        .toList());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:constraints", "Constraint violation", ex, meta);
    }

    // 401 – kredensial tidak valid
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCreds(HttpServletRequest req, InvalidCredentialsException ex) {
        return respond(req, HttpStatus.UNAUTHORIZED, "invalid_credentials", "Invalid credentials", ex, null);
    }

    // 401 – refresh token invalid
    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<?> invalidRefresh(HttpServletRequest req, RefreshTokenInvalidException ex) {
        return respond(req, HttpStatus.UNAUTHORIZED, "invalid_refresh_token", "Invalid refresh token", ex, null);
    }

    // 409 – conflict (username/email taken, atau DB unique constraint)
    @ExceptionHandler({ UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class })
    public ResponseEntity<?> userConflict(HttpServletRequest req, RuntimeException ex) {
        String code = (ex instanceof UsernameAlreadyExistsException) ? "username_taken" : "email_taken";
        return respond(req, HttpStatus.CONFLICT, code, "Conflict", ex, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataConflict(HttpServletRequest req, DataIntegrityViolationException ex) {
        return respond(req, HttpStatus.CONFLICT, "data_conflict", "Data integrity violation", ex, null);
    }

    // 404 – kalau ada use case lempar NoSuchElementException
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(HttpServletRequest req, NoSuchElementException ex) {
        return respond(req, HttpStatus.NOT_FOUND, "not_found", "Resource not found", ex, null);
    }

    // 403 – fallback (kalau belum tertangkap handler security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return respond(req, HttpStatus.FORBIDDEN, "forbidden", "Access denied", ex, null);
    }

    // 503 – IAM tidak tersedia (pesanmu saat ini "iam_unavailable:<reason>")
    @ExceptionHandler(IamUnavailableException.class)
    public ResponseEntity<?> iamUnavailable(HttpServletRequest req, IamUnavailableException ex) {
        // kodenya biarkan sama dengan message supaya reason ikut (contoh: iam_unavailable:upstream_4xx)
        return respond(req, HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), "IAM unavailable", ex, null);
    }

    // Map ErrorResponseException dari Spring (mis. dari handler internal)
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<?> springError(HttpServletRequest req, ErrorResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        Map<String,Object> meta = Map.of("detail", String.valueOf(ex.getBody()));
        return respond(req, status, "spring_error", ex.getMessage(), ex, meta);
    }

    // 400 generic untuk IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(HttpServletRequest req, IllegalArgumentException ex) {
        Map<String,Object> meta = Map.of("detail", ex.getMessage());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request", "Bad request", ex, meta);
    }

    // 500 – fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknown(HttpServletRequest req, Exception ex) {
        return respond(req, HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", "Unexpected error", ex, null);
    }

    private ResponseEntity<?> respond(HttpServletRequest req,
                                      HttpStatus status,
                                      String code,
                                      String message,
                                      Throwable ex,
                                      Map<String, Object> meta) {
        // NOTE: 'meta' akan dimasukkan ke field 'upstream' di ApiErrorResponse sebagai konteks tambahan.
        var body = errors.build(req, status, code, message, ex, meta,
                LoggerFactory.getLogger(GlobalExceptionHandler.class));
        return ResponseEntity.status(status).body(body);
    }
}
