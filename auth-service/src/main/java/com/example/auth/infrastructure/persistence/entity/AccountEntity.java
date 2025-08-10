package com.example.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account",
        uniqueConstraints = {@UniqueConstraint(name="uk_account_username", columnNames="username"), @UniqueConstraint(name="uk_account_email", columnNames="email")})
public class AccountEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable=false, length=64)
    private String username;

    @Column(nullable=false, length=128)
    private String email;

    @Column(name="password_hash", nullable=false, length=100)
    private String passwordHash;

    @Column(nullable=false, length=16)
    private String status;

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = "ACTIVE";
    }
}
