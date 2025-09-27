package com.example.profile.web.profile;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.web.mapper.ProfileMapper;
import com.example.profile_service.web.api.ProfileApi;
import com.example.profile_service.web.model.UserProfileResponse;
import com.example.profile_service.web.model.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class ProfileController implements ProfileApi {

    private final ProfileQueries profileQueries;
    private final ProfileCommands profileCommands;

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        UUID userId = requireCurrentUserId();
        UserProfile profile = profileQueries.getByUserId(userId);
        return ResponseEntity.ok(ProfileMapper.toUserProfileResponse(profile));
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@Valid UserProfileUpdateRequest userProfileUpdateRequest) {
        UUID userId = requireCurrentUserId();
        var command = new ProfileCommands.UpdateProfileCommand(
                userProfileUpdateRequest.getFullName(),
                userProfileUpdateRequest.getBio(),
                userProfileUpdateRequest.getPhone(),
                userProfileUpdateRequest.getAvatarObjectKey()
        );
        UserProfile profile = profileCommands.upsertProfile(userId, command);
        return ResponseEntity.ok(ProfileMapper.toUserProfileResponse(profile));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('profile:profile:read')")
    public ResponseEntity<UserProfileResponse> getProfileById(UUID userId) {
        UserProfile profile = profileQueries.getByUserId(userId);
        return ResponseEntity.ok(ProfileMapper.toUserProfileResponse(profile));
    }

    private UUID requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication present");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getSubject());
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwtPrincipal) {
            return UUID.fromString(jwtPrincipal.getSubject());
        }
        if (authentication.getName() != null) {
            return UUID.fromString(authentication.getName());
        }
        throw new IllegalStateException("Cannot resolve current user");
    }
}
