package com.example.iam.config;

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
                                           InternalServiceTokenFilter internalServiceTokenFilter) throws Exception {
        // Converter: ambil claim "roles" → jadikan ROLE_*
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(this::extractAuthoritiesFromRoles);

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // health/info bebas
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // internal call dari Auth Service:
                // NOTE: untuk endpoint internal ini, kita ijinkan EITHER INTERNAL OR ADMIN
                .requestMatchers(HttpMethod.GET, "/entitlements/**").hasAnyRole("INTERNAL","ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("INTERNAL","ADMIN")

                // IAM RBAC:
                .requestMatchers("/roles/**", "/permissions/**", "/assign/**").hasRole("ADMIN")

                // /authz/check → harus authenticated (role apapun)
                .requestMatchers(HttpMethod.POST, "/authz/check").authenticated()

                // sisanya: tutup
                .anyRequest().denyAll()
        );

        // Validasi JWT user (jwks di application.yml)
        http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

        // Filter internal token sebelum bearer JWT
        http.addFilterBefore(internalServiceTokenFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Ambil claim "roles": ["admin", "editor"] → authorities: ["ROLE_ADMIN","ROLE_EDITOR"]
     */
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
