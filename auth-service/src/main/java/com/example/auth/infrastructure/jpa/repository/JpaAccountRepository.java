package com.example.auth.infrastructure.jpa.repository;

import com.example.auth.infrastructure.jpa.entity.JpaAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAccountRepository extends JpaRepository<JpaAccount, UUID> {
    Optional<JpaAccount> findByUsername(String username);
    Optional<JpaAccount> findByEmail(String email);
    Optional<JpaAccount> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
}
