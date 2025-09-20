package com.example.auth.infrastructure.jpa.repository;

import com.example.auth.infrastructure.jpa.entity.JpaAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface JpaAccountRepository extends JpaRepository<JpaAccount, UUID>, JpaSpecificationExecutor<JpaAccount> {
    Optional<JpaAccount> findByUsername(String username);
    Optional<JpaAccount> findByEmail(String email);
    Optional<JpaAccount> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
}
