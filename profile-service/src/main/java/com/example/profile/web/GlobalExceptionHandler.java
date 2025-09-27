package com.example.profile.web;

import com.example.common.web.response.ErrorResponseBuilder;
import com.example.common.web.error.ApiException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseBuilder errors;


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> apiException(HttpServletRequest req, ApiException ex) {
        Map<String, Object> extra = ex.meta().isEmpty() ? null : Map.of("meta", ex.meta());
        return respond(req, ex.status(), ex.code(), ex.message(), ex, extra);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> badJson(HttpServletRequest req, HttpMessageNotReadableException ex) {
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:malformed_json", "Malformed JSON body", ex, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> beanValidation(HttpServletRequest req, MethodArgumentNotValidException ex) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", Objects.requireNonNullElse(fe.getDefaultMessage(), "invalid")))
                .toList());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:validation", "Validation failed", ex, extra);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolation(HttpServletRequest req, ConstraintViolationException ex) {
        Map<String, Object> extra = Map.of("errors", ex.getConstraintViolations().stream()
                .map(cv -> Map.of("property", cv.getPropertyPath().toString(), "message", cv.getMessage()))
                .toList());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:constraints", "Constraint violation", ex, extra);
    }

    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<?> notFound(HttpServletRequest req, RuntimeException ex) {
        return respond(req, HttpStatus.NOT_FOUND, "not_found", "Resource not found", ex, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpServletRequest req, HttpRequestMethodNotSupportedException ex) {
        return respond(req, HttpStatus.METHOD_NOT_ALLOWED, "method_not_allowed", ex.getMessage(), ex, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> unsupportedMediaType(HttpServletRequest req, HttpMediaTypeNotSupportedException ex) {
        return respond(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported_media_type", ex.getMessage(), ex, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataConflict(HttpServletRequest req, DataIntegrityViolationException ex) {
        return respond(req, HttpStatus.CONFLICT, "data_conflict", "Data integrity violation", ex, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return respond(req, HttpStatus.FORBIDDEN, "forbidden", "Access denied", ex, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknown(HttpServletRequest req, Exception ex) {
        return respond(req, HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", "Unexpected error", ex, null);
    }

    private ResponseEntity<?> respond(HttpServletRequest req,
                                      HttpStatus status,
                                      String code,
                                      String message,
                                      Throwable ex,
                                      Map<String, Object> extra) {
        var body = errors.build(req, status, code, message, ex,
                extra,
                org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class));
        return ResponseEntity.status(status).body(body);
    }
}
