package com.example.auth.web.error;

import com.example.common.web.error.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "invalid_credentials", "Invalid credentials",
                Map.of("errors", List.of(
                        Map.of("field","usernameOrEmail", "message","invalid"),
                        Map.of("field","password",        "message","invalid")
                ))
        );
    }
}
