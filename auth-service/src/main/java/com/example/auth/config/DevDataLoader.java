package com.example.auth.config;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevDataLoader {
    @Bean
    public CommandLineRunner seed(AccountRepository accounts, PasswordHasher hasher) {
        return args -> {
            String enabled = System.getenv().getOrDefault("APP_DEV_SEED", "true");
            if (!Boolean.parseBoolean(enabled)) return;
            accounts.findByUsername("admin").ifPresent(a -> { });
            if (accounts.findByUsername("admin").isEmpty()) {
                Account a = Account.of(null, "admin", "admin@local.test", hasher.encode("admin123"), "ACTIVE", null);
                accounts.save(a);
            }
        };
    }
}
