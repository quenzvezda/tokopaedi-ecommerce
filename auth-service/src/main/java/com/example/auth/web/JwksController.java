package com.example.auth.web;

import com.example.auth.application.jwk.JwkQueries;
import com.example.auth_service.web.api.JwkApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController implements JwkApi {
    private final JwkQueries jwkQueries;

    @Override
    public org.springframework.http.ResponseEntity<Object> getJwks() {
        return org.springframework.http.ResponseEntity.ok((Object) jwkQueries.jwks());
    }
}
