package com.example.auth.web.error;

import com.example.common.web.error.ApiException;
import org.springframework.http.HttpStatus;

public class RefreshTokenInvalidException extends ApiException {
    public RefreshTokenInvalidException() {
        super(HttpStatus.UNAUTHORIZED, "invalid_refresh_token", "Invalid refresh token");
    }
}
