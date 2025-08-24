package com.example.auth.web;

import com.example.common.web.error.ApiException;
import com.example.common.web.response.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseBuilder errors;

    /* ============== Domain-aware (uniform) ============== */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> apiAware(HttpServletRequest req, ApiException ex) {
        return respond(req, ex.status(), ex.code(), ex.message(), ex, ex.meta());
    }

    /* ============== 4xx lain (client errors) ============== */

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
                .map(fe -> Map.of("field", fe.getField(),
                        "message", Objects.toString(fe.getDefaultMessage(), "")))
                .toList());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:validation", "Validation failed", ex, meta);
    }

    // 400 – validasi param/path
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolation(HttpServletRequest req, ConstraintViolationException ex) {
        Map<String, Object> meta = Map.of("errors",
                ex.getConstraintViolations().stream()
                        .map(cv -> Map.of("property", cv.getPropertyPath().toString(),
                                "message", cv.getMessage()))
                        .toList());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request:constraints", "Constraint violation", ex, meta);
    }

    // 404 – routing tidak ketemu (endpoint salah)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> noHandler(HttpServletRequest req, NoHandlerFoundException ex) {
        Map<String,Object> meta = Map.of(
                "path", ex.getRequestURL(),
                "method", ex.getHttpMethod()
        );
        return respond(req, HttpStatus.NOT_FOUND, "not_found", "Resource not found", ex, meta);
    }

    // 405 – method tidak cocok
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpServletRequest req, HttpRequestMethodNotSupportedException ex) {
        Map<String,Object> meta = Map.of("supported", ex.getSupportedHttpMethods());
        return respond(req, HttpStatus.METHOD_NOT_ALLOWED, "method_not_allowed", "HTTP method not allowed", ex, meta);
    }

    // 415 – media type tidak cocok
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> mediaType(HttpServletRequest req, HttpMediaTypeNotSupportedException ex) {
        Map<String,Object> meta = Map.of("supported", ex.getSupportedMediaTypes());
        return respond(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported_media_type", "Unsupported media type", ex, meta);
    }

    // 409 – konflik integritas data (fallback DB unique, dsb)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataConflict(HttpServletRequest req, DataIntegrityViolationException ex) {
        return respond(req, HttpStatus.CONFLICT, "data_conflict", "Data integrity violation", ex, null);
    }

    // 403 – fallback (kalau belum tertangkap handler security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return respond(req, HttpStatus.FORBIDDEN, "forbidden", "Access denied", ex, null);
    }

    // Spring’s ErrorResponseException → bungkus proporsional
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<?> springError(HttpServletRequest req, ErrorResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        Map<String,Object> meta = Map.of("detail", String.valueOf(ex.getBody()));
        return respond(req, status, "spring_error", ex.getMessage(), ex, meta);
    }

    // 400 – generic bad request (untuk IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(HttpServletRequest req, IllegalArgumentException ex) {
        Map<String,Object> meta = Map.of("detail", ex.getMessage());
        return respond(req, HttpStatus.BAD_REQUEST, "bad_request", "Bad request", ex, meta);
    }

    // 500 – fallback terakhir
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknown(HttpServletRequest req, Exception ex) {
        return respond(req, HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", "Unexpected error", ex, null);
    }

    /* ============== Util ============== */
    private ResponseEntity<?> respond(HttpServletRequest req,
                                      HttpStatus status,
                                      String code,
                                      String message,
                                      Throwable ex,
                                      Map<String, Object> meta) {
        var body = errors.build(
                req, status, code, message, ex, meta,
                LoggerFactory.getLogger(GlobalExceptionHandler.class)
        );
        return ResponseEntity.status(status).body(body);
    }
}
