package com.example.catalog.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaCategory {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "parent_id", columnDefinition = "uuid")
    private UUID parentId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
