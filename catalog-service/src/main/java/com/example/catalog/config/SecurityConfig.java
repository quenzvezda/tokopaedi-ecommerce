package com.example.catalog.config;

import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import com.example.common.web.security.JwtAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity  // untuk @PreAuthorize di controller
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        // baca claim "roles" → ROLE_*
        // Jika butuh scope juga: pakai JwtAuthorityUtils.rolesAndScopes("roles", true)
        return JwtAuthorityUtils.rolesOnly("roles");
    }

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http,
                                           JsonAuthEntryPoint entryPoint,
                                           JsonAccessDeniedHandler deniedHandler,
                                           JwtAuthenticationConverter jwtAuthConverter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(rc -> rc.disable())

                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**").permitAll()
                        .requestMatchers("/actuator/health","/actuator/info").permitAll()

                        // contoh sisa template (boleh hapus kalau tak perlu)
                        .requestMatchers(HttpMethod.GET, "/api/v1/ping").permitAll()
                        .requestMatchers("/api/v1/secure/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/v1/echo/**").authenticated()

                        // admin
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN","CATALOG_EDITOR")

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

        return http.build();
    }
}
