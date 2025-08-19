package com.example.catalog.config;

import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JsonAuthEntryPoint entryPoint,
                                           JsonAccessDeniedHandler deniedHandler) throws Exception {

        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        // jwtAuthConverter.setJwtGrantedAuthoritiesConverter(...); // customize if needed

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(rc -> rc.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health","/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ping").permitAll()
                .requestMatchers("/api/v1/secure/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/echo/**").authenticated()
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
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
