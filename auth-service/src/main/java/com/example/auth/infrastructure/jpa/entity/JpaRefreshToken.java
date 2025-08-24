package com.example.auth.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "refresh_token", indexes = @Index(name="idx_refresh_account", columnList = "account_id"))
public class JpaRefreshToken {
    @Id @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name="account_id", nullable=false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name="expires_at", nullable=false)
    private OffsetDateTime expiresAt;

    @Column(nullable=false)
    private boolean revoked;

    @PrePersist
    void prePersist() { if (id == null) id = UUID.randomUUID(); }
}
