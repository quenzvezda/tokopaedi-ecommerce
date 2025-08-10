package com.example.auth.application.query;

import com.example.auth.domain.port.JwtPort;

import java.util.Map;

public class GetJwksQuery {
    private final JwtPort jwt;
    public GetJwksQuery(JwtPort jwt) { this.jwt = jwt; }
    public Map<String, Object> handle() { return jwt.currentJwks(); }
}
