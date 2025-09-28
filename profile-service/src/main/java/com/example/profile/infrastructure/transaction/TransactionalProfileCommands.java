package com.example.profile.infrastructure.transaction;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.store.StoreProfile;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Objects;
import java.util.UUID;

public class TransactionalProfileCommands implements ProfileCommands {

    private final TransactionOperations txOps;
    private final ProfileCommands delegate;

    public TransactionalProfileCommands(TransactionOperations txOps, ProfileCommands delegate) {
        this.txOps = Objects.requireNonNull(txOps, "txOps");
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public UserProfile upsertProfile(UUID userId, UpdateProfileCommand command) {
        return txOps.execute(status -> delegate.upsertProfile(userId, command));
    }

    @Override
    public UserProfile createInitialProfile(UUID userId, CreateInitialProfileCommand command) {
        return txOps.execute(status -> delegate.createInitialProfile(userId, command));
    }

    @Override
    public PresignedUrl prepareAvatarUpload(UUID userId, AvatarUploadCommand command) {
        return delegate.prepareAvatarUpload(userId, command);
    }

    @Override
    public StoreProfile createStore(UUID ownerId, CreateStoreCommand command) {
        return txOps.execute(status -> delegate.createStore(ownerId, command));
    }

    @Override
    public StoreProfile updateStore(UUID ownerId, UUID storeId, UpdateStoreCommand command) {
        return txOps.execute(status -> delegate.updateStore(ownerId, storeId, command));
    }
}
