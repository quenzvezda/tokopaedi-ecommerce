package com.example.auth.web.error;

import com.example.common.web.error.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class EmailAlreadyExistsException extends ApiException {
    public EmailAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "email_taken", "Conflict",
                Map.of("errors", List.of(Map.of(
                        "field", "email",
                        "message", "already taken"
                )))
        );
    }
}
