package com.example.profile.domain.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private UUID userId;
    private String fullName;
    private String bio;
    private String phone;
    private String avatarObjectKey;
    private Instant createdAt;
    private Instant updatedAt;
}
