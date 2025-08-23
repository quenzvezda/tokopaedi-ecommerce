package com.example.catalog.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Konfigurasi client IAM untuk fetch entitlements.
 */
@Data
@ConfigurationProperties(prefix = "iam.client")
public class IamClientProperties {
    /**
     * Base URL IAM (disarankan pakai service-id agar lewat LoadBalancer).
     */
    private String baseUrl = "http://iam-service";

    /**
     * Path entitlements pada iam-service.
     * Contoh: /internal/v1/entitlements/{accountId}
     */
    private String entitlementsPath = "/internal/v1/entitlements/{accountId}";

    /**
     * Header & value untuk auth internal antar service (wajib cocok dengan IAM).
     * Misal: X-Internal-Token: <secret>
     */
    private String internalAuthHeader = "X-Internal-Token";
    private String internalAuthValue;

    /**
     * Prefix authority untuk scopes (mis. 'SCOPE_' atau '' jika ingin tanpa prefix).
     */
    private String scopePrefix = "SCOPE_";

    /**
     * Konfigurasi cache L1 (Caffeine).
     */
    private long cacheTtlMinutes = 10;
    private long cacheMaxSize = 50_000;
}
