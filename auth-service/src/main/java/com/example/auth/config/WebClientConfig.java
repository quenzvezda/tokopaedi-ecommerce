package com.example.auth.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
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
    @Bean
    public WebClient iamWebClient(@Value("${iam.base-url}") String baseUrl,
                                  @Value("${iam.s2s-token}") String token,
                                  @Value("${iam.http.connect-timeout-ms}") int connectMs,
                                  @Value("${iam.http.response-timeout-ms}") int responseMs) {
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectMs)
                .responseTimeout(Duration.ofMillis(responseMs))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(responseMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(responseMs, TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(http))
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(c -> c.defaultCodecs().maxInMemorySize(256 * 1024)).build())
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() { return CircuitBreakerRegistry.ofDefaults(); }

    @Bean
    public RetryRegistry retryRegistry() { return RetryRegistry.ofDefaults(); }
}
