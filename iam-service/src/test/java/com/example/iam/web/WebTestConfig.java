package com.example.iam.web;

import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
class WebTestConfig {

    @Bean
    ErrorProps errorProps() {
        var p = new ErrorProps();
        p.setVerbose(false); // error.details tidak perlu tampil di test
        return p;
    }

    @Bean
    ErrorResponseBuilder errorResponseBuilder(ErrorProps props) {
        return new ErrorResponseBuilder(props, "iam-service");
    }

    // Security permissive untuk test
    @Bean
    SecurityFilterChain testSecurity(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
        return http.build();
    }
}
