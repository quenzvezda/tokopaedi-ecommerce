package com.example.profile.web.avatar;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.web.mapper.ProfileMapper;
import com.example.profile_service.web.api.ProfileAvatarApi;
import com.example.profile_service.web.model.AvatarUploadRequest;
import com.example.profile_service.web.model.PresignedUrlResponse;
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
public class ProfileAvatarController implements ProfileAvatarApi {

    private final ProfileCommands profileCommands;
    private final ProfileQueries profileQueries;

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    public ResponseEntity<PresignedUrlResponse> requestAvatarUploadUrl(AvatarUploadRequest avatarUploadRequest) {
        UUID userId = currentUserId();
        var command = new ProfileCommands.AvatarUploadCommand(
                avatarUploadRequest != null ? avatarUploadRequest.getFileName() : null,
                avatarUploadRequest != null ? avatarUploadRequest.getContentType() : null
        );
        var result = profileCommands.prepareAvatarUpload(userId, command);
        return ResponseEntity.ok(ProfileMapper.toPresignedUrlResponse(result));
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    public ResponseEntity<PresignedUrlResponse> getAvatarViewUrl() {
        UUID userId = currentUserId();
        var result = profileQueries.getAvatarView(userId);
        return ResponseEntity.ok(ProfileMapper.toPresignedUrlResponse(result));
    }

    private UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication present");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getSubject());
        }
        if (authentication.getName() != null) {
            return UUID.fromString(authentication.getName());
        }
        throw new IllegalStateException("Cannot resolve current user");
    }
}
