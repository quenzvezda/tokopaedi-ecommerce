package com.example.iam.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(Exception ex) {
        return Map.of("error","bad_request","message", ex.getMessage(), "ts", Instant.now().toString());
    }

    @ExceptionHandler(ErrorResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> errorResponse(ErrorResponseException ex) {
        return Map.of("error","bad_request","message", ex.getBody().getDetail(), "ts", Instant.now().toString());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> serverError(Exception ex) {
        return Map.of("error","internal_error","message", ex.getMessage(), "ts", Instant.now().toString());
    }
}
