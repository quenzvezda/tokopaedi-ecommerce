package com.example.auth.domain.model;

import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value @With
public class Entitlements {
    UUID accountId;
    int permVer;
    List<String> roles;
    Instant updatedAt;

    public static Entitlements of(UUID accountId, int permVer, List<String> roles, Instant updatedAt) {
        return new Entitlements(accountId, permVer, roles, updatedAt);
    }
}
