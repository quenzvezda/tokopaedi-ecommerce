package com.example.catalog.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Ping")
public class PingController {

    @GetMapping("/ping")
    @Operation(summary = "Simple ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("message", "pong"));
    }

    @GetMapping("/secure")
    @Operation(summary = "Secured ping", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> secure() {
        return ResponseEntity.ok(Map.of("message", "secured"));
    }

    @PostMapping("/echo")
    @Operation(summary = "Echo text", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> echo(@RequestParam @NotBlank String text) {
        return ResponseEntity.ok(Map.of("echo", text));
    }
}
