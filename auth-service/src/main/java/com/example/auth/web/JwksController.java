package com.example.auth.web;

import com.example.auth.application.query.GetJwksQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {
    private final GetJwksQuery jwks;

    @GetMapping(value="/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> jwks() { return jwks.handle(); }
}
