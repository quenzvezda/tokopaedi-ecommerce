package com.example.auth.application.command;

import com.example.auth.domain.model.Account;
import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.*;
import com.example.auth.web.error.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class LoginCommand {
    private final AccountPort accountPort;
    private final PasswordHasherPort passwordHasher;
    private final IamPort iamPort;
    private final JwtPort jwtPort;
    private final RefreshTokenPort refreshTokenPort;

    public record TokenPair(String tokenType, String accessToken, long expiresIn, String refreshToken) {}

    public TokenPair handle(String usernameOrEmail, String password) {
        Account acc = resolveAccount(usernameOrEmail);
        if (!passwordHasher.matches(password, acc.getPasswordHash())) throw new InvalidCredentialsException();

        Entitlements ent = iamPort.fetchEntitlements(acc.getId());
        String access = jwtPort.generateAccessToken(acc.getId(), ent.getRoles(), ent.getPermVer(), Instant.now());
        RefreshToken ref = refreshTokenPort.create(UUID.randomUUID(), acc.getId(), Instant.now());
        return new TokenPair("Bearer", access, jwtPort.getAccessTtlSeconds(), ref.getId().toString());
    }

    private Account resolveAccount(String usernameOrEmail) {
        boolean looksEmail = usernameOrEmail != null && usernameOrEmail.contains("@");
        if (looksEmail) return accountPort.findByEmail(usernameOrEmail).orElseThrow(InvalidCredentialsException::new);
        return accountPort.findByUsername(usernameOrEmail).orElseThrow(InvalidCredentialsException::new);
    }
}
