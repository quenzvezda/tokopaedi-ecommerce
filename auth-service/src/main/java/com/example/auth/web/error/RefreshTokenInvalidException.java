package com.example.auth.web.error;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException() { super("invalid_refresh_token"); }
}
