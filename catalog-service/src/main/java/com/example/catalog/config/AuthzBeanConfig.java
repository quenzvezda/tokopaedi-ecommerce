package com.example.catalog.config;

import com.example.catalog.application.authz.EntitlementsQuery;
import com.example.catalog.infrastructure.iam.CachedEntitlementsQuery;
import com.example.catalog.security.IamClientProperties;
import com.example.catalog.security.IamEntitlementsClient;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.UUID;

/**
 * Bean factory untuk authz/entitlements (Factory Pattern).
 */
@Configuration
@EnableConfigurationProperties(IamClientProperties.class)
public class AuthzBeanConfig {

    @Bean
    Cache<UUID, Object> entitlementsCache(IamClientProperties props) {
        // L1 cache (Caffeine)
        // TODO: Tambahkan L2 Redis sebagai backplane untuk berbagi antar instance (future)
        return Caffeine.newBuilder()
                .maximumSize(props.getCacheMaxSize())
                .expireAfterWrite(Duration.ofMinutes(props.getCacheTtlMinutes()))
                .build();
    }

    @Bean
    EntitlementsQuery entitlementsQuery(Cache<UUID, Object> entitlementsCache,
                                        IamEntitlementsClient client,
                                        IamClientProperties props) {
        return new CachedEntitlementsQuery(entitlementsCache, client, props);
    }
}
