package com.example.auth.domain.port;

import com.example.auth.domain.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenPort {
    RefreshToken create(UUID id, UUID accountId, Instant now);
    Optional<RefreshToken> findById(UUID id);
    void consume(UUID id);
}
