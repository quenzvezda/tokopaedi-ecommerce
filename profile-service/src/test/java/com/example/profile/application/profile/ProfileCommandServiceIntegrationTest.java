package com.example.profile.application.profile;

import com.example.common.web.error.ApiException;
import com.example.profile.ProfileServiceApplication;
import com.example.profile.domain.profile.SellerRoleGateway;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.profile.UserProfileRepository;
import com.example.profile.infrastructure.jpa.store.JpaStoreProfileRepository;
import com.example.profile.infrastructure.jpa.store.StoreProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = ProfileServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:profile;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.format_sql=false",
        "spring.flyway.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.main.allow-bean-definition-overriding=true",
        "profile.avatar.endpoint=http://localhost:10000",
        "profile.iam.internal-auth-value=test-token"
})
class ProfileCommandServiceIntegrationTest {

    @Autowired
    private ProfileCommands profileCommands;

    @Autowired
    private UserProfileRepository userProfiles;

    @Autowired
    private JpaStoreProfileRepository jpaStoreProfiles;

    @MockBean
    private SellerRoleGateway sellerRoleGateway;

    @BeforeEach
    void resetMocks() {
        reset(sellerRoleGateway);
        jpaStoreProfiles.deleteAll();
    }

    @Test
    void createStore_rollsBackWhenRoleAssignmentFails() {
        UUID ownerId = UUID.randomUUID();
        userProfiles.save(UserProfile.builder()
                .userId(ownerId)
                .fullName("Test User")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        doThrow(ApiException.serviceUnavailable("iam_unavailable", "Failed"))
                .when(sellerRoleGateway).ensureSellerRole(ownerId);

        assertThatThrownBy(() -> profileCommands.createStore(ownerId,
                new ProfileCommands.CreateStoreCommand("Store", "store", "desc")))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("iam_unavailable");

        List<StoreProfileEntity> stores = jpaStoreProfiles.findByOwnerId(ownerId);
        assertThat(stores).isEmpty();
    }

    @Test
    void createStore_persistsStoreAndAssignsRole() {
        UUID ownerId = UUID.randomUUID();
        userProfiles.save(UserProfile.builder()
                .userId(ownerId)
                .fullName("Another User")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        profileCommands.createStore(ownerId, new ProfileCommands.CreateStoreCommand("My Shop", "my-shop", "desc"));

        List<StoreProfileEntity> stores = jpaStoreProfiles.findByOwnerId(ownerId);
        assertThat(stores).hasSize(1);
        assertThat(stores.get(0).getSlug()).isEqualTo("my-shop");

        verify(sellerRoleGateway).ensureSellerRole(ownerId);
        verifyNoMoreInteractions(sellerRoleGateway);
    }
}
