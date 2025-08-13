package com.example.auth.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient iamWebClient(
            WebClient.Builder builder,
            @Value("${iam.base-url}") String baseUrl,
            @Value("${iam.service-token}") String serviceToken,
            @Value("${webclient.connect-timeout-ms:300}") int connectTimeoutMs,
            @Value("${webclient.read-timeout-ms:800}") int readTimeoutMs) {
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                );

        // Filter kecil untuk log header — sangat membantu debug
        ExchangeFilterFunction logHeaders = (request, next) -> {
            String hdr = request.headers().getFirst("X-Internal-Token");
            if (hdr == null) {
                log.warn("iamWebClient: X-Internal-Token ABSENT for {} {}", request.method(), request.url());
            } else {
                log.debug("iamWebClient: X-Internal-Token present for {} {}", request.method(), request.url());
            }
            return next.exchange(request);
        };

        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(http))
                .defaultHeader("X-Internal-Token", serviceToken)
                .filter(logHeaders)
                .build();
    }
}
