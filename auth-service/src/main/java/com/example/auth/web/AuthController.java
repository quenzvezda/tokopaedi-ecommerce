package com.example.auth.web;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RefreshCommand;
import com.example.auth.application.command.RegisterCommand;
import com.example.auth.web.dto.LoginRequest;
import com.example.auth.web.dto.RefreshRequest;
import com.example.auth.web.dto.RegisterRequest;
import com.example.auth.web.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginCommand loginCommand;
    private final RefreshCommand refreshCommand;
    private final RegisterCommand registerCommand;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        UUID id = registerCommand.handle(req);
        return ResponseEntity.created(URI.create("/accounts/" + id))
                .body(Map.of("message", "registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody LoginRequest req) {
        LoginCommand.TokenPair pair = loginCommand.handle(req.getUsernameOrEmail(), req.getPassword());
        TokenResponse body = new TokenResponse(pair.tokenType(), pair.accessToken(), pair.expiresIn(), pair.refreshToken());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Validated @RequestBody RefreshRequest req) {
        LoginCommand.TokenPair pair = refreshCommand.handle(req.getRefreshToken());
        TokenResponse body = new TokenResponse(pair.tokenType(), pair.accessToken(), pair.expiresIn(), pair.refreshToken());
        return ResponseEntity.ok(body);
    }
}
