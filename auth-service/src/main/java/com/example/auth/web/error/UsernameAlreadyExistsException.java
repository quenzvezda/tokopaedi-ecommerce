package com.example.auth.web.error;

import com.example.common.web.error.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class UsernameAlreadyExistsException extends ApiException {
    public UsernameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "username_taken", "Conflict",
                Map.of("errors", List.of(Map.of(
                        "field", "username",
                        "message", "already taken"
                )))
        );
    }
}
