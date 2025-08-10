package com.example.auth.config;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RefreshCommand;
import com.example.auth.application.query.GetJwksQuery;
import com.example.auth.domain.port.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Period;

@Configuration
public class BeanConfig {
    @Bean
    public LoginCommand loginCommand(AccountPort accounts, PasswordHasherPort hasher, IamPort iam, JwtPort jwt, RefreshTokenPort refresh, JwtSettings settings) {
        Duration accessTtl = Duration.parse(settings.getAccessTtl());
        Period refreshTtl = Period.parse(settings.getRefreshTtl());
        return new LoginCommand(accounts, hasher, iam, jwt, refresh, accessTtl, refreshTtl);
    }

    @Bean
    public RefreshCommand refreshCommand(RefreshTokenPort refresh, IamPort iam, JwtPort jwt, JwtSettings settings) {
        Duration accessTtl = Duration.parse(settings.getAccessTtl());
        Period refreshTtl = Period.parse(settings.getRefreshTtl());
        return new RefreshCommand(refresh, iam, jwt, accessTtl, refreshTtl);
    }

    @Bean
    public GetJwksQuery getJwksQuery(JwtPort jwt) { return new GetJwksQuery(jwt); }
}
