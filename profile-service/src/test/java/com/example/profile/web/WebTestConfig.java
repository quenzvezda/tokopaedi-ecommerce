package com.example.profile.web;

import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebTestConfig {
    @Bean
    ErrorResponseBuilder errorResponseBuilder() {
        ErrorProps props = new ErrorProps();
        props.setVerbose(false);
        return new ErrorResponseBuilder(props, "profile-service");
    }
}
