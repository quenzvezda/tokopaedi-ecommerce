package com.example.profile.web.store;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.web.mapper.ProfileMapper;
import com.example.profile_service.web.api.ProfileStoreApi;
import com.example.profile_service.web.model.StoreCreateRequest;
import com.example.profile_service.web.model.StoreProfileResponse;
import com.example.profile_service.web.model.StoreUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class ProfileStoreController implements ProfileStoreApi {

    private final ProfileCommands profileCommands;
    private final ProfileQueries profileQueries;

    @Override
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<List<StoreProfileResponse>> listMyStores() {
        UUID userId = requireCurrentUserId();
        var stores = profileQueries.listStores(userId).stream()
                .map(ProfileMapper::toStoreProfileResponse)
                .toList();
        return ResponseEntity.ok(stores);
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    public ResponseEntity<StoreProfileResponse> createStore(@Valid StoreCreateRequest storeCreateRequest) {
        UUID userId = requireCurrentUserId();
        var command = new ProfileCommands.CreateStoreCommand(
                storeCreateRequest.getName(),
                storeCreateRequest.getSlug(),
                storeCreateRequest.getDescription()
        );
        var store = profileCommands.createStore(userId, command);
        return ResponseEntity.status(201).body(ProfileMapper.toStoreProfileResponse(store));
    }

    @Override
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<StoreProfileResponse> updateStore(UUID storeId, @Valid StoreUpdateRequest storeUpdateRequest) {
        UUID userId = requireCurrentUserId();
        var command = new ProfileCommands.UpdateStoreCommand(
                storeUpdateRequest.getName(),
                storeUpdateRequest.getSlug(),
                storeUpdateRequest.getDescription(),
                storeUpdateRequest.getActive()
        );
        var store = profileCommands.updateStore(userId, storeId, command);
        return ResponseEntity.ok(ProfileMapper.toStoreProfileResponse(store));
    }

    private UUID requireCurrentUserId() {
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
