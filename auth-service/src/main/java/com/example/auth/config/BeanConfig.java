package com.example.auth.config;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RefreshCommand;
import com.example.auth.application.command.RegisterCommand;
import com.example.auth.application.query.GetJwksQuery;
import com.example.auth.domain.port.*;
import com.example.auth.infrastructure.adapter.*;
import com.example.auth.infrastructure.persistence.repo.AccountJpaRepository;
import com.example.auth.infrastructure.persistence.repo.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {
    private final AccountJpaRepository accountRepo;
    private final RefreshTokenJpaRepository refreshRepo;
    private final JwtSettings jwtSettings;

    @Bean public AccountPort accountPort() { return new AccountJpaAdapter(accountRepo); }
    @Bean public RefreshTokenPort refreshTokenPort() { return new RefreshTokenJpaAdapter(refreshRepo); }
    @Bean public PasswordHasherPort passwordHasherPort(PasswordEncoder enc) { return new PasswordHasherAdapter(enc); }
    @Bean public JwtPort jwtPort() { return new JwtNimbusAdapter(jwtSettings); }
    @Bean public IamPort iamPort(WebClient iamWebClient, @Value("${iam.http.response-timeout-ms}") int responseMs) { return new IamWebClientAdapter(iamWebClient, responseMs); }

    @Bean public RegisterCommand registerCommand(AccountPort a, PasswordHasherPort ph) { return new RegisterCommand(a, ph); }
    @Bean public LoginCommand loginCommand(AccountPort a, PasswordHasherPort ph, IamPort iam, JwtPort jwt, RefreshTokenPort rt) { return new LoginCommand(a, ph, iam, jwt, rt); }
    @Bean public RefreshCommand refreshCommand(RefreshTokenPort rt, IamPort iam, JwtPort jwt) { return new RefreshCommand(rt, iam, jwt); }
    @Bean public GetJwksQuery getJwksQuery(JwtPort jwt) { return new GetJwksQuery(jwt); }
}
