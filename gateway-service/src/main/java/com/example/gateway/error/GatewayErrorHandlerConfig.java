package com.example.gateway.error;

import com.example.common.web.ErrorProps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;

@Configuration
public class GatewayErrorHandlerConfig {

    @Bean
    public ErrorAttributes gatewayErrorAttributes(@Value("${spring.application.name:gateway-service}") String serviceName, ErrorProps props) {
        return new GatewayErrorAttributes(serviceName, props);
    }

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes, ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
        var resources = new WebProperties.Resources();
        var errorProps = new ErrorProperties();
        var handler = new DefaultErrorWebExceptionHandler(errorAttributes, resources, errorProps, applicationContext);

        handler.setMessageWriters(serverCodecConfigurer.getWriters());
        handler.setMessageReaders(serverCodecConfigurer.getReaders());
        return handler;
    }
}
