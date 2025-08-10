package com.example.auth.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    @Value("${iam.base-url}") private String iamBaseUrl;
    @Value("${iam.service-token}") private String serviceToken;
    @Value("${webclient.connect-timeout-ms}") private int connectMs;
    @Value("${webclient.read-timeout-ms}") private int readMs;

    @Bean
    public WebClient iamWebClient() {
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectMs)
                .responseTimeout(Duration.ofMillis(readMs))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(readMs, TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .baseUrl(iamBaseUrl)
                .defaultHeader("Authorization", "Bearer " + serviceToken)
                .clientConnector(new ReactorClientHttpConnector(http))
                .exchangeStrategies(ExchangeStrategies.builder().codecs(c -> c.defaultCodecs().maxInMemorySize(512 * 1024)).build())
                .build();
    }
}
