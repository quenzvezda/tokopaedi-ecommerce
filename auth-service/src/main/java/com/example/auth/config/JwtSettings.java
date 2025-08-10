package com.example.auth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtSettings {
    private String issuer;
    private String audience;
    private String accessTtl;
    private String refreshTtl;
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public void setAudience(String audience) { this.audience = audience; }
    public void setAccessTtl(String accessTtl) { this.accessTtl = accessTtl; }
    public void setRefreshTtl(String refreshTtl) { this.refreshTtl = refreshTtl; }
}
