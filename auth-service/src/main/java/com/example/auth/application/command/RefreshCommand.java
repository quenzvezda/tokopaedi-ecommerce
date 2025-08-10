package com.example.auth.application.command;

import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class RefreshCommand {
    private final RefreshTokenPort refreshTokens;
    private final IamPort iam;
    private final JwtPort jwt;
    private final Duration accessTtl;
    private final Period refreshTtl;

    public RefreshCommand(RefreshTokenPort refreshTokens, IamPort iam, JwtPort jwt, Duration accessTtl, Period refreshTtl) {
        this.refreshTokens = refreshTokens; this.iam = iam; this.jwt = jwt; this.accessTtl = accessTtl; this.refreshTtl = refreshTtl;
    }

    public LoginCommand.TokenPair handle(UUID refreshTokenId) {
        RefreshToken t = refreshTokens.findActive(refreshTokenId).orElseThrow(() -> new IllegalStateException("refresh_not_found"));
        if (t.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) { refreshTokens.revoke(t.getId()); throw new IllegalStateException("refresh_expired"); }
        refreshTokens.revoke(t.getId());
        RefreshToken nt = refreshTokens.issue(t.getAccountId(), OffsetDateTime.now(ZoneOffset.UTC).plus(refreshTtl));
        List<String> roles = iam.getUserRoles(t.getAccountId());
        int pv = iam.getPermVersion(t.getAccountId());
        String access = jwt.generateAccessToken(t.getAccountId(), roles, pv, java.time.Instant.now());
        return new LoginCommand.TokenPair(access, nt.getId().toString(), jwt.getAccessTtlSeconds());
    }
}
