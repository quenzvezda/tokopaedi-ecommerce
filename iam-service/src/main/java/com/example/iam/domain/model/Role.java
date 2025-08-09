package com.example.iam.domain.model;

import lombok.Value;
import lombok.With;

@Value
@With
public class Role {
    Long id;
    String name;

    public static Role ofNew(String name) { return new Role(null, name); }
}
