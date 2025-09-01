package com.example.auth.web;

import com.example.auth.application.account.AccountCommands;
import com.example.auth.application.auth.AuthCommands;
import com.example.auth.web.dto.LoginRequest;
import com.example.auth.web.dto.RefreshRequest;
import com.example.auth.web.dto.RegisterRequest;
import com.example.auth.web.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. Auth")
public class AuthController {

    private final AuthCommands authCommands;
    private final AccountCommands accountCommands;

    @PostMapping("/register")
    @Operation(operationId = "auth_1_register", summary = "Register account")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        UUID id = accountCommands.register(req.getUsername(), req.getEmail(), req.getPassword());
        return ResponseEntity.created(URI.create("/accounts/" + id))
                .body(Map.of("message", "registered"));
    }

    @PostMapping("/login")
    @Operation(operationId = "auth_2_login", summary = "Login")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody LoginRequest req) {
        AuthCommands.TokenPair pair = authCommands.login(req.getUsernameOrEmail(), req.getPassword());
        TokenResponse body = new TokenResponse(pair.tokenType(), pair.accessToken(), pair.expiresIn(), pair.refreshToken());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/refresh")
    @Operation(operationId = "auth_3_refresh", summary = "Refresh token")
    public ResponseEntity<TokenResponse> refresh(@Validated @RequestBody RefreshRequest req) {
        AuthCommands.TokenPair pair = authCommands.refresh(req.getRefreshToken());
        TokenResponse body = new TokenResponse(pair.tokenType(), pair.accessToken(), pair.expiresIn(), pair.refreshToken());
        return ResponseEntity.ok(body);
    }
}
