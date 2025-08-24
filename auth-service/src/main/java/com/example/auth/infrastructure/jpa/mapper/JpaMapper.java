package com.example.auth.infrastructure.jpa.mapper;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.token.RefreshToken;
import com.example.auth.infrastructure.jpa.entity.JpaAccount;
import com.example.auth.infrastructure.jpa.entity.JpaRefreshToken;

public final class JpaMapper {
    private JpaMapper() {}

    // Account
    public static Account toDomain(JpaAccount e) {
        if (e == null) return null;
        return Account.of(e.getId(), e.getUsername(), e.getEmail(),
                e.getPasswordHash(), e.getStatus(), e.getCreatedAt());
    }

    public static JpaAccount toEntity(Account d) {
        if (d == null) return null;
        JpaAccount e = new JpaAccount();
        e.setId(d.getId());
        e.setUsername(d.getUsername());
        e.setEmail(d.getEmail());
        e.setPasswordHash(d.getPasswordHash());
        e.setStatus(d.getStatus());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }

    // RefreshToken
    public static RefreshToken toDomain(JpaRefreshToken e) {
        if (e == null) return null;
        return RefreshToken.of(e.getId(), e.getAccountId(), e.getExpiresAt(), e.isRevoked());
    }

    public static JpaRefreshToken toEntity(RefreshToken d) {
        if (d == null) return null;
        JpaRefreshToken e = new JpaRefreshToken();
        e.setId(d.getId());
        e.setAccountId(d.getAccountId());
        e.setExpiresAt(d.getExpiresAt());
        e.setRevoked(d.isRevoked());
        return e;
    }
}
