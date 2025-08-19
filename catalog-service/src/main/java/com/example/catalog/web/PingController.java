package com.example.catalog.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v1")
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("message", "pong"));
    }

    @GetMapping("/secure")
    public ResponseEntity<?> secure() {
        return ResponseEntity.ok(Map.of("message", "secured"));
    }

    @PostMapping("/echo")
    public ResponseEntity<?> echo(@RequestParam @NotBlank String text) {
        return ResponseEntity.ok(Map.of("echo", text));
    }
}
