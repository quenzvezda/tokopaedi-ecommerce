package com.example.catalog.web;

import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import com.example.catalog.application.authz.EntitlementsQuery;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.UUID;

/**
 * Test configuration to provide common beans and disable security for MVC tests.
 */
@TestConfiguration
public class WebTestConfig {

    @Bean
    ErrorProps errorProps() {
        var p = new ErrorProps();
        p.setVerbose(false);
        return p;
    }

    @Bean
    ErrorResponseBuilder errorResponseBuilder(ErrorProps props) {
        return new ErrorResponseBuilder(props, "catalog-service");
    }

    @Bean
    SecurityFilterChain testSecurity(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    EntitlementsQuery entitlementsQuery() {
        return (UUID accountId, Integer tokenPermVer) -> List.of();
    }
}
