package com.example.catalog.domain.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private UUID id;
    private UUID parentId; // nullable
    private String name;
    private boolean active;
    private Integer sortOrder; // nullable
}
