package com.example.auth.config;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RefreshCommand;
import com.example.auth.application.query.GetJwksQuery;
import com.example.auth.domain.port.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Period;

@Configuration
public class BeanConfig {
    @Bean
    public LoginCommand loginCommand(AccountPort accounts, PasswordHasherPort hasher, IamPort iam, JwtPort jwt, RefreshTokenPort refresh, EntitlementsStorePort store,
                                     JwtSettings jwtSettings, @Value("${iam.cache-ttl}") String cacheTtl) {
        Duration accessTtl = Duration.parse(jwtSettings.getAccessTtl());
        Period refreshTtl = Period.parse(jwtSettings.getRefreshTtl());
        return new LoginCommand(accounts, hasher, iam, jwt, refresh, store, Duration.parse(cacheTtl), accessTtl, refreshTtl);
    }

    @Bean
    public RefreshCommand refreshCommand(RefreshTokenPort refresh, IamPort iam, JwtPort jwt, EntitlementsStorePort store,
                                         JwtSettings jwtSettings, @Value("${iam.cache-ttl}") String cacheTtl) {
        Duration accessTtl = Duration.parse(jwtSettings.getAccessTtl());
        Period refreshTtl = Period.parse(jwtSettings.getRefreshTtl());
        return new RefreshCommand(refresh, iam, jwt, store, Duration.parse(cacheTtl), accessTtl, refreshTtl);
    }

    @Bean
    public GetJwksQuery getJwksQuery(JwtPort jwt) { return new GetJwksQuery(jwt); }
}
