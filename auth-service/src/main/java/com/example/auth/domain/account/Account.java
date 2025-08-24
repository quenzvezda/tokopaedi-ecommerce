package com.example.auth.domain.account;

import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value @With
public class Account {
    UUID id;
    String username;
    String email;
    String passwordHash;
    String status;
    OffsetDateTime createdAt;

    public static Account of(UUID id, String username, String email, String passwordHash, String status, OffsetDateTime createdAt) {
        return new Account(id, username, email, passwordHash, status, createdAt);
    }
}
