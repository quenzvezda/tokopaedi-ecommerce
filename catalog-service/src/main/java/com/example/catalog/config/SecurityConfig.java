package com.example.catalog.config;

import com.example.catalog.security.ScopeAuthorityAugmentorFilter;
import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import com.example.common.web.security.JwtAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security config: roles dari JWT (ROLE_* via helper), scopes di-augment via entitlements filter.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        // Roles dari klaim "roles" → ROLE_*
        return JwtAuthorityUtils.rolesOnly("roles");
    }

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http,
                                           JsonAuthEntryPoint entryPoint,
                                           JsonAccessDeniedHandler deniedHandler,
                                           JwtAuthenticationConverter jwtAuthConverter,
                                           ScopeAuthorityAugmentorFilter scopeAugmentor) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(RequestCacheConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // Public catalog reads
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**").permitAll()
                        .requestMatchers("/actuator/health","/actuator/info").permitAll()

                        // Admin endpoints (role-based)
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN","CATALOG_EDITOR")

                        // Legacy sample
                        .requestMatchers(HttpMethod.GET, "/api/v1/ping").permitAll()
                        .requestMatchers("/api/v1/secure/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/echo/**").authenticated()

                        .anyRequest().denyAll()
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

        // Augment scopes SETELAH JWT ter-authenticate
        http.addFilterAfter(scopeAugmentor, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}
