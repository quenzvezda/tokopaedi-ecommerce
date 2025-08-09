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
@Table(name = "user_role",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_role", columnNames = {"account_id","role_id"}),
        indexes = {@Index(name="idx_ur_account", columnList = "account_id"), @Index(name="idx_ur_role", columnList = "role_id")})
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="account_id", nullable=false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name="role_id", nullable=false)
    private Long roleId;
}
