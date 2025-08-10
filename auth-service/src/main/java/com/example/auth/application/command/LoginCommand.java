package com.example.auth.application.command;

import com.example.auth.domain.model.Account;
import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class LoginCommand {
    private final AccountPort accounts;
    private final PasswordHasherPort hasher;
    private final IamPort iam;
    private final JwtPort jwt;
    private final RefreshTokenPort refreshTokens;
    private final Duration accessTtl;
    private final Period refreshTtl;

    public LoginCommand(AccountPort accounts, PasswordHasherPort hasher, IamPort iam, JwtPort jwt, RefreshTokenPort refreshTokens, Duration accessTtl, Period refreshTtl) {
        this.accounts = accounts; this.hasher = hasher; this.iam = iam; this.jwt = jwt; this.refreshTokens = refreshTokens; this.accessTtl = accessTtl; this.refreshTtl = refreshTtl;
    }

    public TokenPair handle(String usernameOrEmail, String rawPassword) {
        Account a = accounts.findByUsername(usernameOrEmail).orElseGet(() -> accounts.findByEmail(usernameOrEmail).orElseThrow(() -> new IllegalArgumentException("account_not_found")));
        if (!"ACTIVE".equalsIgnoreCase(a.getStatus())) throw new IllegalStateException("account_inactive");
        if (!hasher.matches(rawPassword, a.getPasswordHash())) throw new IllegalStateException("invalid_credentials");
        List<String> roles = iam.getUserRoles(a.getId());
        int permVer = iam.getPermVersion(a.getId());
        String access = jwt.generateAccessToken(a.getId(), roles, permVer, java.time.Instant.now());
        RefreshToken rt = refreshTokens.issue(a.getId(), OffsetDateTime.now(ZoneOffset.UTC).plus(refreshTtl));
        return new TokenPair(access, rt.getId().toString(), jwt.getAccessTtlSeconds());
    }

    public record TokenPair(String accessToken, String refreshToken, long expiresInSeconds) {}
}
