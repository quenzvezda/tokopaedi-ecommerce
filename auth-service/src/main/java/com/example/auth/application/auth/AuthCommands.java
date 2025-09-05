package com.example.auth.application.auth;

public interface AuthCommands {

    record TokenPair(String tokenType, String accessToken, long expiresIn, String refreshToken) {}

    /**
     * Login dengan username/email + password → TokenPair (access + refresh).
     */
    TokenPair login(String usernameOrEmail, String password);

    /**
     * Rotating refresh-token → TokenPair baru.
     */
    TokenPair refresh(String refreshToken);

    /**
     * Logout: revoke refresh token if present. Idempotent.
     */
    void logout(String refreshToken);
}
