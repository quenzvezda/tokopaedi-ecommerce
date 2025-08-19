package com.example.catalog.config;

import com.example.common.web.ErrorProps;
import com.example.common.web.ErrorResponseBuilder;
import com.example.common.web.RequestIdFilter;
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
        ErrorProps p = new ErrorProps();
        p.setVerbose(verbose);
        return p;
    }

    @Bean
    public ErrorResponseBuilder errorResponseBuilder(
            ErrorProps props,
            @Value("${spring.application.name:catalog-service}") String serviceName
    ) {
        return new ErrorResponseBuilder(props, serviceName);
    }

    @Bean
    public FilterRegistrationBean<Filter> requestIdFilter() {
        var frb = new FilterRegistrationBean<Filter>(new RequestIdFilter());
        frb.setOrder(0);
        return frb;
    }

    @Bean
    public JsonAuthEntryPoint jsonAuthEntryPoint(ErrorResponseBuilder builder, ObjectMapper om) {
        return new JsonAuthEntryPoint(builder, om);
    }

    @Bean
    public JsonAccessDeniedHandler jsonAccessDeniedHandler(ErrorResponseBuilder builder, ObjectMapper om) {
        return new JsonAccessDeniedHandler(builder, om);
    }
}
