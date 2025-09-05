package com.example.auth.web;

import com.example.auth.application.account.AccountCommands;
import com.example.auth.application.auth.AuthCommands;
import com.example.auth.config.JwtSettings;
import com.example.auth.web.dto.AccessTokenResponse;
import com.example.auth.web.dto.LoginRequest;
import com.example.auth.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. Auth")
public class AuthController {

    private final AuthCommands authCommands;
    private final AccountCommands accountCommands;
    private final JwtSettings jwtSettings;

    @Value("${REFRESH_COOKIE_NAME:refresh_token}")
    private String refreshCookieName;
    @Value("${REFRESH_COOKIE_PATH:/}")
    private String refreshCookiePath;
    @Value("${REFRESH_COOKIE_DOMAIN:}")
    private String refreshCookieDomain;
    @Value("${REFRESH_COOKIE_SAMESITE:Lax}")
    private String refreshCookieSameSite;
    @Value("${REFRESH_COOKIE_SECURE:false}")
    private boolean refreshCookieSecure;

    private ResponseCookie buildRefreshCookie(String value, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(refreshCookieName, value)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path(refreshCookiePath)
                .sameSite(refreshCookieSameSite)
                .maxAge(Duration.ofSeconds(maxAgeSeconds));
        if (refreshCookieDomain != null && !refreshCookieDomain.isBlank()) {
            b.domain(refreshCookieDomain);
        }
        return b.build();
    }

    private long refreshTtlSeconds() {
        try {
            return Duration.parse(jwtSettings.getRefreshTtl()).toSeconds();
        } catch (Exception e) {
            return 7L * 24 * 3600; // fallback 7 days
        }
    }

    @PostMapping("/register")
    @Operation(operationId = "auth_1_register", summary = "Register account")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        UUID id = accountCommands.register(req.getUsername(), req.getEmail(), req.getPassword());
        return ResponseEntity.created(URI.create("/accounts/" + id))
                .body(Map.of("message", "registered"));
    }

    @PostMapping("/login")
    @Operation(operationId = "auth_2_login", summary = "Login")
    public ResponseEntity<AccessTokenResponse> login(@Validated @RequestBody LoginRequest req) {
        AuthCommands.TokenPair pair = authCommands.login(req.getUsernameOrEmail(), req.getPassword());
        long maxAge = refreshTtlSeconds();
        ResponseCookie cookie = buildRefreshCookie(pair.refreshToken(), maxAge);
        AccessTokenResponse body = new AccessTokenResponse(pair.tokenType(), pair.accessToken(), pair.expiresIn());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(body);
    }

    @PostMapping("/refresh")
    @Operation(operationId = "auth_3_refresh", summary = "Refresh token")
    public ResponseEntity<AccessTokenResponse> refresh(
            jakarta.servlet.http.HttpServletRequest request
    ) {
        String refreshCookie = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie c : request.getCookies()) {
                if (refreshCookieName.equals(c.getName())) { refreshCookie = c.getValue(); break; }
            }
        }
        if (refreshCookie == null || refreshCookie.isBlank()) {
            // simulate invalid refresh token error path
            throw new com.example.auth.web.error.RefreshTokenInvalidException();
        }
        AuthCommands.TokenPair pair = authCommands.refresh(refreshCookie);
        long maxAge = refreshTtlSeconds();
        ResponseCookie cookie = buildRefreshCookie(pair.refreshToken(), maxAge);
        AccessTokenResponse body = new AccessTokenResponse(pair.tokenType(), pair.accessToken(), pair.expiresIn());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(body);
    }

    @PostMapping("/logout")
    @Operation(operationId = "auth_4_logout", summary = "Logout")
    public ResponseEntity<Void> logout(jakarta.servlet.http.HttpServletRequest request) {
        String refreshCookie = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie c : request.getCookies()) {
                if (refreshCookieName.equals(c.getName())) { refreshCookie = c.getValue(); break; }
            }
        }
        if (refreshCookie != null && !refreshCookie.isBlank()) {
            authCommands.logout(refreshCookie);
        }
        // clear cookie
        ResponseCookie cleared = buildRefreshCookie("", 0);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .build();
    }
}
