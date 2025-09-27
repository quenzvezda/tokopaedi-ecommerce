package com.example.profile.domain.profile;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository {
    Optional<UserProfile> findByUserId(UUID userId);

    UserProfile save(UserProfile profile);
}
