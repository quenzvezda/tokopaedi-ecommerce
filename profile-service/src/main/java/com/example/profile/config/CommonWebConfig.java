package com.example.profile.config;

import com.example.common.web.filter.RequestIdFilter;
import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import com.example.common.web.security.JsonAccessDeniedHandler;
import com.example.common.web.security.JsonAuthEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonWebConfig {

    @Bean
    public ErrorProps errorProps(@Value("${app.errors.verbose:false}") boolean verbose) {
        ErrorProps props = new ErrorProps();
        props.setVerbose(verbose);
        return props;
    }

    @Bean
    public ErrorResponseBuilder errorResponseBuilder(ErrorProps props,
                                                     @Value("${spring.application.name:profile-service}") String serviceName) {
        return new ErrorResponseBuilder(props, serviceName);
    }

    @Bean
    public FilterRegistrationBean<Filter> requestIdFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new RequestIdFilter());
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public JsonAuthEntryPoint jsonAuthEntryPoint(ErrorResponseBuilder builder, ObjectMapper objectMapper) {
        return new JsonAuthEntryPoint(builder, objectMapper);
    }

    @Bean
    public JsonAccessDeniedHandler jsonAccessDeniedHandler(ErrorResponseBuilder builder, ObjectMapper objectMapper) {
        return new JsonAccessDeniedHandler(builder, objectMapper);
    }
}
