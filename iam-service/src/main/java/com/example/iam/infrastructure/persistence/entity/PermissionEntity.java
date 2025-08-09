package com.example.iam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permission", uniqueConstraints = @UniqueConstraint(name = "uk_permission_name", columnNames = "name"))
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=128)
    private String name;

    @Column(length=255)
    private String description;
}
