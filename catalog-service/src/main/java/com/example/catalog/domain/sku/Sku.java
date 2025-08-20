package com.example.catalog.domain.sku;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sku {
    private UUID id;
    private UUID productId;
    private String skuCode;
    private boolean active;
    private String barcode; // nullable
}
