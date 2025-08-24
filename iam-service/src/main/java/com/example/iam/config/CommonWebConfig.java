package com.example.iam.config;

import com.example.common.web.filter.RequestIdFilter;
import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import com.example.common.web.security.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;

@Configuration
public class CommonWebConfig {

    @Bean
    public ErrorProps errorProps(@Value("${app.errors.verbose:false}") boolean verbose) {
        ErrorProps p = new ErrorProps();
        p.setVerbose(verbose);
        return p;
    }

    @Bean
    public ErrorResponseBuilder errorResponseBuilder(ErrorProps props, @Value("${spring.application.name:iam-service}") String serviceName) {
        return new ErrorResponseBuilder(props, serviceName);
    }

    @Bean
    public FilterRegistrationBean<Filter> requestIdFilter() {
        FilterRegistrationBean<Filter> frb = new FilterRegistrationBean<>(new RequestIdFilter());
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
