package com.example.auth.config;

import com.example.auth.application.account.AccountCommandService;
import com.example.auth.application.account.AccountCommands;
import com.example.auth.application.auth.AuthCommandService;
import com.example.auth.application.auth.AuthCommands;
import com.example.auth.application.jwk.JwkQueries;
import com.example.auth.application.jwk.JwkQueryService;

import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.domain.entitlement.EntitlementClient;
import com.example.auth.domain.token.RefreshTokenRepository;
import com.example.auth.domain.token.jwt.JwtProvider;

import com.example.auth.infrastructure.iam.IamEntitlementClientImpl;
import com.example.auth.infrastructure.jpa.AccountRepositoryImpl;
import com.example.auth.infrastructure.jpa.RefreshTokenRepositoryImpl;
import com.example.auth.infrastructure.jpa.repository.JpaAccountRepository;
import com.example.auth.infrastructure.jpa.repository.JpaRefreshTokenRepository;
import com.example.auth.infrastructure.jwt.JwtProviderImpl;
import com.example.auth.infrastructure.security.PasswordHasherImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final JwtSettings jwtSettings;

    // =========================================================
    // ===============  INFRASTRUCTURE / ADAPTERS  =============
    // =========================================================

    // ---- JPA repositories (adapter to domain.*Repository) ----
    @Bean
    public AccountRepository accountRepository(JpaAccountRepository jpa) {
        return new AccountRepositoryImpl(jpa);
    }

    @Bean
    public RefreshTokenRepository refreshTokenRepository(JpaRefreshTokenRepository jpa) {
        return new RefreshTokenRepositoryImpl(jpa);
    }

    // ---- Security / hashing ----
    @Bean
    public PasswordHasher passwordHasher(PasswordEncoder encoder) {
        return new PasswordHasherImpl(encoder);
    }

    // ---- JWT provider (Nimbus) ----
    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProviderImpl(jwtSettings);
    }

    // ---- IAM client (WebClient → internal entitlements) ----
    @Bean
    public EntitlementClient entitlementClient(WebClient iamWebClient,
                                               @Value("${iam.http.response-timeout-ms}") int responseMs,
                                               @Value("${iam.service-token}") String serviceToken) {
        return new IamEntitlementClientImpl(iamWebClient, responseMs, serviceToken);
    }

    // =========================================================
    // ==================  APPLICATION LAYER  ==================
    // =========================================================

    // ---- Account slice (commands) ----
    @Bean
    public AccountCommands accountCommands(AccountRepository accountRepository,
                                           PasswordHasher passwordHasher) {
        return new AccountCommandService(accountRepository, passwordHasher);
    }

    // ---- Auth slice (commands) ----
    @Bean
    public AuthCommands authCommands(AccountRepository accountRepository,
                                     PasswordHasher passwordHasher,
                                     EntitlementClient entitlementClient,
                                     JwtProvider jwtProvider,
                                     RefreshTokenRepository refreshTokenRepository) {
        return new AuthCommandService(
                accountRepository, passwordHasher, entitlementClient, jwtProvider, refreshTokenRepository
        );
    }

    // ---- JWKs slice (queries) ----
    @Bean
    public JwkQueries jwkQueries(JwtProvider jwtProvider) {
        return new JwkQueryService(jwtProvider);
    }
}
