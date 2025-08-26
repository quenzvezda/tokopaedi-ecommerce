package com.example.auth.application.auth;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.domain.entitlement.EntitlementClient;
import com.example.auth.domain.entitlement.Entitlements;
import com.example.auth.domain.token.RefreshToken;
import com.example.auth.domain.token.RefreshTokenRepository;
import com.example.auth.domain.token.jwt.JwtProvider;
import com.example.auth.web.error.InvalidCredentialsException;
import com.example.auth.web.error.RefreshTokenInvalidException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthCommandService implements AuthCommands {

    private final AccountRepository accountRepository;
    private final PasswordHasher passwordHasher;
    private final EntitlementClient entitlementClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public TokenPair login(String usernameOrEmail, String password) {
        Account acc = accountRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(InvalidCredentialsException::new);
        if (!passwordHasher.matches(password, acc.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        Entitlements ent = entitlementClient.fetchEntitlements(acc.getId());
        String access = jwtProvider.generateAccessToken(
                acc.getId(), ent.getRoles(), ent.getPermVer(), Instant.now()
        );
        RefreshToken ref = refreshTokenRepository.create(UUID.randomUUID(), acc.getId(), Instant.now());
        return new TokenPair("Bearer", access, jwtProvider.getAccessTtlSeconds(), ref.getId().toString());
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        UUID tokenId = UUID.fromString(refreshToken);
        RefreshToken current = refreshTokenRepository.findById(tokenId)
                .orElseThrow(RefreshTokenInvalidException::new);

        // rotate
        refreshTokenRepository.consume(current.getId());
        RefreshToken rotated = refreshTokenRepository.create(UUID.randomUUID(), current.getAccountId(), Instant.now());

        Entitlements ent = entitlementClient.fetchEntitlements(current.getAccountId());
        String access = jwtProvider.generateAccessToken(
                current.getAccountId(), ent.getRoles(), ent.getPermVer(), Instant.now()
        );

        return new TokenPair("Bearer", access, jwtProvider.getAccessTtlSeconds(), rotated.getId().toString());
    }
}
