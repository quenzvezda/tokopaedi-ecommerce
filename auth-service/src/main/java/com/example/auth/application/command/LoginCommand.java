package com.example.auth.application.command;

import com.example.auth.domain.model.Account;
import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class LoginCommand {
    private final AccountPort accounts;
    private final PasswordHasherPort hasher;
    private final IamPort iam;
    private final JwtPort jwt;
    private final RefreshTokenPort refreshTokens;
    private final EntitlementsStorePort store;
    private final Duration cacheTtl;
    private final Duration accessTtl;
    private final Period refreshTtl;

    public LoginCommand(AccountPort accounts, PasswordHasherPort hasher, IamPort iam, JwtPort jwt, RefreshTokenPort refreshTokens, EntitlementsStorePort store, Duration cacheTtl, Duration accessTtl, Period refreshTtl) {
        this.accounts = accounts; this.hasher = hasher; this.iam = iam; this.jwt = jwt; this.refreshTokens = refreshTokens; this.store = store; this.cacheTtl = cacheTtl; this.accessTtl = accessTtl; this.refreshTtl = refreshTtl;
    }

    public TokenPair handle(String usernameOrEmail, String rawPassword) {
        Account a = accounts.findByUsername(usernameOrEmail).orElseGet(() -> accounts.findByEmail(usernameOrEmail).orElseThrow(() -> new IllegalArgumentException("account_not_found")));
        if (!"ACTIVE".equalsIgnoreCase(a.getStatus())) throw new IllegalStateException("account_inactive");
        if (!hasher.matches(rawPassword, a.getPasswordHash())) throw new IllegalStateException("invalid_credentials");
        Entitlements e = store.get(a.getId()).filter(this::isFresh).orElseGet(() -> fetchAndUpdate(a.getId()));
        if (e == null) throw new IllegalStateException("iam_unavailable");
        String access = jwt.generateAccessToken(a.getId(), e.getRoles(), e.getPermVer(), Instant.now());
        RefreshToken rt = refreshTokens.issue(a.getId(), OffsetDateTime.now(ZoneOffset.UTC).plus(refreshTtl));
        return new TokenPair(access, rt.getId().toString(), accessTtl.toSeconds());
    }

    private boolean isFresh(Entitlements e) { return e.getUpdatedAt().isAfter(Instant.now().minus(cacheTtl)); }

    private Entitlements fetchAndUpdate(UUID accountId) {
        try { return store.upsertIfNewer(iam.fetchEntitlements(accountId)); } catch (RuntimeException ex) { return store.get(accountId).orElse(null); }
    }

    public record TokenPair(String accessToken, String refreshToken, long expiresInSeconds) {}
}
