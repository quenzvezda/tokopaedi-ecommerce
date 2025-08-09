package com.example.iam.domain.model;

import lombok.Value;
import lombok.With;

@Value
@With
public class Permission {
    Long id;
    String name;
    String description;

    public static Permission ofNew(String name, String description) { return new Permission(null, name, description); }
}
