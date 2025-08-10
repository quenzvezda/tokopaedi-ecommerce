package com.example.auth.domain.port;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JwtPort {
    String generateAccessToken(UUID sub, List<String> roles, int permVer, Instant now);
    long getAccessTtlSeconds();
    Map<String, Object> currentJwks();
}
