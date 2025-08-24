package com.example.common.web.error;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Exception standar untuk REST API.
 * - Selalu punya HttpStatus, code (string pendek), message (human message), dan meta (untuk 'upstream').
 * - Bisa langsung "throw new ApiException(HttpStatus.CONFLICT, ...)" atau pakai factory method statis.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String message;
    private final Map<String, Object> meta;

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, null);
    }

    public ApiException(HttpStatus status, String code, String message, Map<String, Object> meta) {
        super(code); // supaya log ringkas: code jadi pesan utama
        this.status  = Objects.requireNonNull(status);
        this.code    = Objects.requireNonNull(code);
        this.message = Objects.requireNonNullElse(message, status.getReasonPhrase());
        this.meta    = (meta == null ? Collections.emptyMap() : Map.copyOf(meta));
    }

    public HttpStatus status() { return status; }
    public String code()       { return code; }
    public String message()    { return message; }
    public Map<String, Object> meta() { return meta; }

    /* ---------- Factory methods agar lebih ringkas dipakai di service ---------- */
    public static ApiException badRequest(String code, String message, Map<String,Object> meta) {
        return new ApiException(HttpStatus.BAD_REQUEST, code, message, meta);
    }
    public static ApiException unauthorized(String code, String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, code, message);
    }
    public static ApiException forbidden(String code, String message) {
        return new ApiException(HttpStatus.FORBIDDEN, code, message);
    }
    public static ApiException notFound(String code, String message) {
        return new ApiException(HttpStatus.NOT_FOUND, code, message);
    }
    public static ApiException conflict(String code, String message) {
        return new ApiException(HttpStatus.CONFLICT, code, message);
    }
    public static ApiException serviceUnavailable(String code, String message) {
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, code, message);
    }
}
