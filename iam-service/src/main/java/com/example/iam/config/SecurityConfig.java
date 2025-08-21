package com.example.iam.config;

import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import com.example.common.web.security.JwtAuthorityUtils;
import com.example.iam.security.InternalServiceTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public InternalServiceTokenFilter internalServiceTokenFilter(InternalAuthProperties props) {
        return new InternalServiceTokenFilter(props);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        // Seragam: klaim "roles" → ROLE_*
        return JwtAuthorityUtils.rolesOnly("roles");
        // Jika juga ingin menggabungkan scope:
        // return JwtAuthorityUtils.rolesAndScopes("roles", true);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           InternalServiceTokenFilter internalServiceTokenFilter,
                                           JsonAuthEntryPoint authEntryPoint,
                                           JsonAccessDeniedHandler accessDeniedHandler,
                                           JwtAuthenticationConverter jwtAuthConverter) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // Actuator bebas
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // INTERNAL API (dipanggil Auth-Service) → INTERNAL atau ADMIN
                .requestMatchers("/internal/v1/**").hasAnyRole("INTERNAL","ADMIN")

                // ADMIN API
                .requestMatchers("/api/v1/roles/**",
                        "/api/v1/permissions/**",
                        "/api/v1/assign/**").hasRole("ADMIN")

                // /api/v1/authz/check → butuh JWT user (role apapun)
                .requestMatchers(HttpMethod.POST, "/api/v1/authz/check").authenticated()

                // sisanya tutup
                .anyRequest().denyAll()
        );

        // Handler JSON dari common-web
        http.exceptionHandling(h -> h
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // Resource Server JWT + converter roles
        http.oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // Filter internal-token sebelum Bearer JWT
        http.addFilterBefore(internalServiceTokenFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}
