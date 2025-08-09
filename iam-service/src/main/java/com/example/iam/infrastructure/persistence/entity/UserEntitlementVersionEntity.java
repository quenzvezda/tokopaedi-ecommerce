package com.example.iam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_entitlement_version",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_version", columnNames = "account_id"),
        indexes = @Index(name="idx_user_version", columnList = "account_id"))
public class UserEntitlementVersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="account_id", nullable=false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name="version", nullable=false)
    private Integer version;
}
