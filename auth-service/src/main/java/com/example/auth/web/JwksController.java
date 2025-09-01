package com.example.auth.web;

import com.example.auth.application.jwk.JwkQueries;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "2. JWK")
public class JwksController {
    private final JwkQueries jwkQueries;

    @GetMapping(value="/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "jwk_1_keys", summary = "Get JWKS")
    public Map<String,Object> jwks() {
        return jwkQueries.jwks();
    }
}
