package com.example.profile.config;

import com.example.profile.application.profile.ProfileCommandService;
import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueryService;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.application.registration.AccountRegistrationHandler;
import com.example.profile.domain.avatar.AvatarStorageService;
import com.example.profile.domain.profile.SellerRoleGateway;
import com.example.profile.domain.profile.UserProfileRepository;
import com.example.profile.domain.store.StoreProfileRepository;
import com.example.profile.infrastructure.iam.IamSellerRoleGateway;
import com.example.profile.infrastructure.jpa.profile.JpaUserProfileRepository;
import com.example.profile.infrastructure.jpa.profile.UserProfileRepositoryImpl;
import com.example.profile.infrastructure.jpa.store.JpaStoreProfileRepository;
import com.example.profile.infrastructure.jpa.store.StoreProfileRepositoryImpl;
import com.example.profile.infrastructure.minio.MinioAvatarStorageService;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({AvatarStorageProperties.class, ProfileIamProperties.class})
@RequiredArgsConstructor
public class BeanConfig {

    private final AvatarStorageProperties avatarStorageProperties;
    private final ProfileIamProperties profileIamProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient.Builder builder = MinioClient.builder().endpoint(avatarStorageProperties.getEndpoint());
        if (avatarStorageProperties.getAccessKey() != null && avatarStorageProperties.getSecretKey() != null) {
            builder = builder.credentials(avatarStorageProperties.getAccessKey(), avatarStorageProperties.getSecretKey());
        }
        return builder.build();
    }

    @Bean
    public AvatarStorageService avatarStorageService(MinioClient minioClient) {
        return new MinioAvatarStorageService(minioClient, avatarStorageProperties);
    }

    @Bean
    public UserProfileRepository userProfileRepository(JpaUserProfileRepository jpaRepository) {
        return new UserProfileRepositoryImpl(jpaRepository);
    }

    @Bean
    public StoreProfileRepository storeProfileRepository(JpaStoreProfileRepository jpaRepository) {
        return new StoreProfileRepositoryImpl(jpaRepository);
    }

    @Bean
    @LoadBalanced
    public RestTemplate iamRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        Duration timeout = Duration.ofMillis(profileIamProperties.getRequestTimeoutMs());
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }

    @Bean
    public SellerRoleGateway sellerRoleGateway(RestTemplate iamRestTemplate) {
        return new IamSellerRoleGateway(iamRestTemplate, profileIamProperties);
    }

    @Bean
    public ProfileCommands profileCommands(UserProfileRepository userProfiles,
                                           StoreProfileRepository storeProfiles,
                                           AvatarStorageService avatarStorageService,
                                           SellerRoleGateway sellerRoleGateway) {
        return new ProfileCommandService(userProfiles, storeProfiles, avatarStorageService, sellerRoleGateway);
    }

    @Bean
    public ProfileQueries profileQueries(UserProfileRepository userProfiles,
                                         StoreProfileRepository storeProfiles,
                                         AvatarStorageService avatarStorageService) {
        return new ProfileQueryService(userProfiles, storeProfiles, avatarStorageService);
    }

    @Bean
    public AccountRegistrationHandler accountRegistrationHandler(ProfileCommands profileCommands) {
        return new AccountRegistrationHandler(profileCommands);
    }
}
