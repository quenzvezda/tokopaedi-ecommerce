package com.example.auth.application.jwk;

import com.example.auth.domain.token.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class JwkQueryService implements JwkQueries {
    private final JwtProvider jwt;
    @Override public Map<String, Object> jwks() { return jwt.currentJwks(); }
}
