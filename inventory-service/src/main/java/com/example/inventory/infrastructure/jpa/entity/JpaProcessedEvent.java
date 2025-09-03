package com.example.inventory.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaProcessedEvent {
    @Id
    @Column(name = "event_id", columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 160)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}

