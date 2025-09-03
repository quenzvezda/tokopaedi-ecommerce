package com.example.inventory.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaStockItem {
    @Id
    @Column(name = "sku_id", columnDefinition = "uuid")
    private UUID skuId;

    @Column(name = "product_id", columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "qty_on_hand", nullable = false)
    private int qtyOnHand;

    @Column(name = "reserved", nullable = false)
    private int reserved;

    @Column(name = "sellable", nullable = false)
    private boolean sellable;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

