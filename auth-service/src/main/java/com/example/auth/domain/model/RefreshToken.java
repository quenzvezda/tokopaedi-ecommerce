package com.example.auth.domain.model;

import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value @With
public class RefreshToken {
    UUID id;
    UUID accountId;
    OffsetDateTime expiresAt;
    boolean revoked;

    public static RefreshToken of(UUID id, UUID accountId, OffsetDateTime expiresAt, boolean revoked) {
        return new RefreshToken(id, accountId, expiresAt, revoked);
    }
}
