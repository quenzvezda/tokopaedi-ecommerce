package com.example.gateway.config;

import com.example.common.web.response.ErrorProps;
import com.example.common.web.filter.RequestIdWebFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayWebConfig {

    @Bean
    public ErrorProps errorProps(@Value("${app.errors.verbose:false}") boolean verbose) {
        ErrorProps p = new ErrorProps();
        p.setVerbose(verbose);
        return p;
    }

    @Bean
    public RequestIdWebFilter requestIdWebFilter() {
        return new RequestIdWebFilter();
    }
}
