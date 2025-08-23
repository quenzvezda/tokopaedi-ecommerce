package com.example.iam.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role_permission",
        uniqueConstraints = @UniqueConstraint(name = "uk_role_permission", columnNames = {"role_id","permission_id"}),
        indexes = {@Index(name="idx_rp_role", columnList = "role_id"), @Index(name="idx_rp_permission", columnList = "permission_id")})
public class RolePermissionJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="role_id", nullable=false)
    private Long roleId;

    @Column(name="permission_id", nullable=false)
    private Long permissionId;
}
