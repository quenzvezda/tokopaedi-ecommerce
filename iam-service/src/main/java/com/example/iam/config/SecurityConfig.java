package com.example.iam.config;

import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import com.example.iam.security.InternalServiceTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public InternalServiceTokenFilter internalServiceTokenFilter(InternalAuthProperties props) {
        return new InternalServiceTokenFilter(props);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           InternalServiceTokenFilter internalServiceTokenFilter,
                                           JsonAuthEntryPoint authEntryPoint,
                                           JsonAccessDeniedHandler accessDeniedHandler) throws Exception {
        // Converter: klaim "roles" -> authorities ROLE_*
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(this::extractAuthoritiesFromRoles);

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

        // >>>> Gunakan handler JSON dari modul common
        http.exceptionHandling(h -> h
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // Validasi JWT user (jwks sudah dikonfigurasi di application.yml / bean JwtDecoder)
        http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // Filter internal-token sebelum Bearer JWT
        http.addFilterBefore(internalServiceTokenFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    private Collection<GrantedAuthority> extractAuthoritiesFromRoles(Jwt jwt) {
        Object claim = jwt.getClaim("roles");
        if (claim instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(r -> "ROLE_" + r.toUpperCase(Locale.ROOT))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
