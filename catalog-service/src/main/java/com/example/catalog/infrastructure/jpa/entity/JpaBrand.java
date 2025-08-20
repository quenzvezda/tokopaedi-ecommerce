package com.example.catalog.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "brands")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaBrand {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 160, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active;
}
