package com.example.auth.application.command;

import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.UUID;

public class RefreshCommand {
    private final RefreshTokenPort refreshTokens;
    private final IamPort iam;
    private final JwtPort jwt;
    private final EntitlementsStorePort store;
    private final Duration cacheTtl;
    private final Duration accessTtl;
    private final Period refreshTtl;

    public RefreshCommand(RefreshTokenPort refreshTokens, IamPort iam, JwtPort jwt, EntitlementsStorePort store, Duration cacheTtl, Duration accessTtl, Period refreshTtl) {
        this.refreshTokens = refreshTokens; this.iam = iam; this.jwt = jwt; this.store = store; this.cacheTtl = cacheTtl; this.accessTtl = accessTtl; this.refreshTtl = refreshTtl;
    }

    public LoginCommand.TokenPair handle(UUID refreshTokenId) {
        RefreshToken t = refreshTokens.findActive(refreshTokenId).orElseThrow(() -> new IllegalStateException("refresh_not_found"));
        if (t.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) { refreshTokens.revoke(t.getId()); throw new IllegalStateException("refresh_expired"); }
        refreshTokens.revoke(t.getId());
        RefreshToken nt = refreshTokens.issue(t.getAccountId(), OffsetDateTime.now(ZoneOffset.UTC).plus(refreshTtl));
        Entitlements e = store.get(t.getAccountId()).filter(this::isFresh).orElseGet(() -> fetchAndUpdate(t.getAccountId()));
        if (e == null) throw new IllegalStateException("iam_unavailable");
        String access = jwt.generateAccessToken(t.getAccountId(), e.getRoles(), e.getPermVer(), Instant.now());
        return new LoginCommand.TokenPair(access, nt.getId().toString(), accessTtl.toSeconds());
    }

    private boolean isFresh(Entitlements e) { return e.getUpdatedAt().isAfter(Instant.now().minus(cacheTtl)); }

    private Entitlements fetchAndUpdate(UUID accountId) {
        try { return store.upsertIfNewer(iam.fetchEntitlements(accountId)); } catch (RuntimeException ex) { return store.get(accountId).orElse(null); }
    }
}
