package com.example.iam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class JwtDecoderConfig {

    @Bean
    @LoadBalanced
    RestTemplate lbRestTemplate(RestTemplateBuilder b) {
        return b
                .setConnectTimeout(Duration.ofMillis(500))
                .setReadTimeout(Duration.ofMillis(1000))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(RestTemplate lbRestTemplate, @Value("${auth.jwk-set-uri}") String jwkSetUri) {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(lbRestTemplate) // pakai Load Balancer ke auth-service
                .build();
    }
}
