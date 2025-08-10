package com.example.auth.domain.port;

import com.example.auth.domain.model.RefreshToken;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenPort {
    RefreshToken issue(UUID accountId, OffsetDateTime expiresAt);
    Optional<RefreshToken> findActive(UUID id);
    void revoke(UUID id);
    long deleteExpired(UUID accountId, OffsetDateTime before);
}
