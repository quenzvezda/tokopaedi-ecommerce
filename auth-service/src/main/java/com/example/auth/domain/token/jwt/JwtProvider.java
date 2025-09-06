package com.example.auth.domain.token.jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JwtProvider {
    /**
     * Generate access token with required claims.
     */
    default String generateAccessToken(UUID sub, List<String> roles, int permVer, Instant now) {
        return generateAccessToken(sub, roles, permVer, now, null, null);
    }

    /**
     * Generate access token with optional username and email claims.
     */
    String generateAccessToken(UUID sub, List<String> roles, int permVer, Instant now, String username, String email);

    long getAccessTtlSeconds();
    Map<String, Object> currentJwks();
}
