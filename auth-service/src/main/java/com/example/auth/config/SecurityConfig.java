package com.example.auth.config;

import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import com.example.common.web.security.JwtAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        return JwtAuthorityUtils.rolesOnly("roles");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JsonAuthEntryPoint entryPoint,
                                           JsonAccessDeniedHandler deniedHandler,
                                           JwtAuthenticationConverter jwtAuthConverter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        // Auth endpoints (public)
                        .requestMatchers(
                                "/auth/api/v1/register",
                                "/auth/api/v1/login",
                                "/auth/api/v1/refresh",
                                "/auth/api/v1/logout"
                        ).permitAll()
                        .requestMatchers("/.well-known/**", "/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Anything else = must be authenticated
                        .anyRequest().authenticated()
                )
                .exceptionHandling(h -> h
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
