package com.example.profile.infrastructure.transaction;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.store.StoreProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionalProfileCommandsTest {

    @Mock
    private TransactionOperations txOps;
    @Mock
    private ProfileCommands delegate;

    private TransactionalProfileCommands commands;

    @BeforeEach
    void setUp() {
        commands = new TransactionalProfileCommands(txOps, delegate);
    }

    @Test
    void transactionalMethods_delegateWithinTransaction() {
        when(txOps.execute(any())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            TransactionCallback<Object> cb = (TransactionCallback<Object>) inv.getArgument(0);
            return cb.doInTransaction(mock(TransactionStatus.class));
        });

        UUID userId = UUID.randomUUID();
        var profile = UserProfile.builder().userId(userId).fullName("Alice").build();
        var upsert = new ProfileCommands.UpdateProfileCommand("Alice", null, null, null);
        when(delegate.upsertProfile(userId, upsert)).thenReturn(profile);

        var initial = new ProfileCommands.CreateInitialProfileCommand("Alice", "123");
        when(delegate.createInitialProfile(userId, initial)).thenReturn(profile);

        UUID storeOwner = UUID.randomUUID();
        var createStore = new ProfileCommands.CreateStoreCommand("Shop", "shop", null);
        var store = StoreProfile.builder()
                .id(UUID.randomUUID())
                .ownerId(storeOwner)
                .name("Shop")
                .slug("shop")
                .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2024-01-01T01:00:00Z"))
                .active(true)
                .build();
        when(delegate.createStore(storeOwner, createStore)).thenReturn(store);

        UUID storeId = store.getId();
        var updateStore = new ProfileCommands.UpdateStoreCommand("Shop", "shop", null, true);
        when(delegate.updateStore(storeOwner, storeId, updateStore)).thenReturn(store);

        assertThat(commands.upsertProfile(userId, upsert)).isSameAs(profile);
        assertThat(commands.createInitialProfile(userId, initial)).isSameAs(profile);
        assertThat(commands.createStore(storeOwner, createStore)).isSameAs(store);
        assertThat(commands.updateStore(storeOwner, storeId, updateStore)).isSameAs(store);

        verify(delegate).upsertProfile(userId, upsert);
        verify(delegate).createInitialProfile(userId, initial);
        verify(delegate).createStore(storeOwner, createStore);
        verify(delegate).updateStore(storeOwner, storeId, updateStore);
        verify(txOps, times(4)).execute(any());
    }

    @Test
    void prepareAvatarUpload_bypassesTransaction() {
        UUID userId = UUID.randomUUID();
        var command = new ProfileCommands.AvatarUploadCommand("avatar.png", "image/png");
        var expected = PresignedUrl.builder()
                .url("https://example")
                .method("PUT")
                .expiresAt(Instant.parse("2024-01-01T00:10:00Z"))
                .build();
        when(delegate.prepareAvatarUpload(userId, command)).thenReturn(expected);

        assertThat(commands.prepareAvatarUpload(userId, command)).isSameAs(expected);
        verify(txOps, never()).execute(any());
        verify(delegate).prepareAvatarUpload(userId, command);
    }
}
