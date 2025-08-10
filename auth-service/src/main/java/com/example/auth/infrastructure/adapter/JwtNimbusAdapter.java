package com.example.auth.infrastructure.adapter;

import com.example.auth.config.JwtSettings;
import com.example.auth.domain.port.JwtPort;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class JwtNimbusAdapter implements JwtPort {
    private final KeyPair keyPair;
    @Getter private final String kid;
    private final JwtSettings settings;
    private final Duration accessTtl;

    public JwtNimbusAdapter(JwtSettings settings) {
        this.settings = settings;
        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
            g.initialize(2048);
            this.keyPair = g.generateKeyPair();
        } catch (Exception e) { throw new IllegalStateException(e); }
        this.kid = UUID.randomUUID().toString();
        this.accessTtl = Duration.parse(settings.getAccessTtl());
    }

    @Override
    public String generateAccessToken(UUID sub, List<String> roles, int permVer, Instant now) {
        Instant exp = now.plus(accessTtl);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(sub.toString())
                .issuer(settings.getIssuer())
                .audience(List.of(settings.getAudience()))
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", roles)
                .claim("perm_ver", permVer)
                .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(kid).type(JOSEObjectType.JWT).build();
        SignedJWT jwt = new SignedJWT(header, claims);
        try { jwt.sign(new RSASSASigner(keyPair.getPrivate())); } catch (JOSEException e) { throw new IllegalStateException(e); }
        return jwt.serialize();
    }

    @Override public long getAccessTtlSeconds() { return accessTtl.toSeconds(); }

    @Override
    public Map<String, Object> currentJwks() {
        RSAKey rsa = new RSAKey.Builder((java.security.interfaces.RSAPublicKey) keyPair.getPublic()).keyID(kid).build();
        return new JWKSet(rsa).toJSONObject();
    }
}
