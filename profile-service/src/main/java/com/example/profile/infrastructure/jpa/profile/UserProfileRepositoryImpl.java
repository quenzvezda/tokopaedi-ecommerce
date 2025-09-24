package com.example.profile.infrastructure.jpa.profile;

import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.profile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final JpaUserProfileRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(UserProfileMapper::toDomain);
    }

    @Override
    @Transactional
    public UserProfile save(UserProfile profile) {
        UserProfileEntity entity = UserProfileMapper.toEntity(profile);
        UserProfileEntity saved = jpaRepository.save(entity);
        return UserProfileMapper.toDomain(saved);
    }
}
