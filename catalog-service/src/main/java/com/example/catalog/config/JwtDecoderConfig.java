package com.example.catalog.config;

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
                .setReadTimeout(Duration.ofMillis(1500))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(RestTemplate lbRestTemplate,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri) {
        // Pakai service-id "auth-service" via LoadBalancer
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(lbRestTemplate)
                .build();
    }
}
