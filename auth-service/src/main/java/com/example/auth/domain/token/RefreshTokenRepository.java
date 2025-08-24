package com.example.auth.domain.token;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken create(UUID id, UUID accountId, Instant now);
    Optional<RefreshToken> findById(UUID id);
    void consume(UUID id);
}
