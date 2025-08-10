package com.example.auth.web;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RefreshCommand;
import com.example.auth.web.dto.LoginRequest;
import com.example.auth.web.dto.RefreshRequest;
import com.example.auth.web.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginCommand login;
    private final RefreshCommand refresh;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        var pair = login.handle(req.getUsernameOrEmail(), req.getPassword());
        return new TokenResponse("Bearer", pair.accessToken(), pair.expiresInSeconds(), pair.refreshToken());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
        var pair = refresh.handle(UUID.fromString(req.getRefresh_token()));
        return new TokenResponse("Bearer", pair.accessToken(), pair.expiresInSeconds(), pair.refreshToken());
    }
}
