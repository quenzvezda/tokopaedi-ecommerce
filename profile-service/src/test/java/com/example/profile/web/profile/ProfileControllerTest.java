package com.example.profile.web.profile;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.web.GlobalExceptionHandler;
import com.example.profile_service.web.model.UserProfileUpdateRequest;
import com.example.common.web.response.ErrorResponseBuilder;
import com.example.common.web.response.ErrorProps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    ProfileQueries profileQueries;
    @Mock
    ProfileCommands profileCommands;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        ErrorProps props = new ErrorProps();
        props.setVerbose(false);
        ErrorResponseBuilder builder = new ErrorResponseBuilder(props, "profile-service");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(builder);
        ProfileController controller = new ProfileController(profileQueries, profileCommands);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyProfile_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_CUSTOMER"));
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .fullName("John Doe")
                .bio("Seller")
                .phone("123")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(profileQueries.getByUserId(userId)).thenReturn(profile);

        mvc.perform(get("/profile/api/v1/profiles/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void updateMyProfile_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_CUSTOMER"));
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .fullName("Jane Doe")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        lenient().when(profileCommands.upsertProfile(any(), any())).thenReturn(profile);

        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/profile/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(new UserProfileUpdateRequest()
                                .fullName("Jane Doe")
                                .bio("New bio"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));

        verify(profileCommands).upsertProfile(any(), any());
    }

    @Test
    void getProfileById_requiresAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(UUID.randomUUID(), List.of("ROLE_ADMIN"));
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .fullName("Admin View")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(profileQueries.getByUserId(userId)).thenReturn(profile);

        mvc.perform(get("/profile/api/v1/profiles/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    private void setAuthentication(UUID subject, List<String> authorities) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject.toString())
                .claim("sub", subject.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        Authentication authentication = new JwtAuthenticationToken(jwt, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
