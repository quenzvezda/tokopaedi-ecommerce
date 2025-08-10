package com.example.auth.application.command;

import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.IamPort;
import com.example.auth.domain.port.JwtPort;
import com.example.auth.domain.port.RefreshTokenPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class RefreshCommand {
    private final RefreshTokenPort refreshTokenPort;
    private final IamPort iamPort;
    private final JwtPort jwtPort;

    public LoginCommand.TokenPair handle(String refreshToken) {
        UUID tokenId = UUID.fromString(refreshToken);
        RefreshToken current = refreshTokenPort.findById(tokenId).orElseThrow(() -> new RuntimeException("invalid_refresh_token"));
        refreshTokenPort.consume(current.getId());

        RefreshToken rotated = refreshTokenPort.create(UUID.randomUUID(), current.getAccountId(), Instant.now());
        Entitlements ent = iamPort.fetchEntitlements(current.getAccountId());
        String access = jwtPort.generateAccessToken(current.getAccountId(), ent.getRoles(), ent.getPermVer(), Instant.now());

        return new LoginCommand.TokenPair("Bearer", access, jwtPort.getAccessTtlSeconds(), rotated.getId().toString());
    }
}
