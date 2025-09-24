package com.example.profile.web.avatar;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.web.GlobalExceptionHandler;
import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
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
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfileAvatarControllerTest {

    @Mock
    ProfileCommands profileCommands;
    @Mock
    ProfileQueries profileQueries;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        ErrorProps props = new ErrorProps();
        props.setVerbose(false);
        ErrorResponseBuilder builder = new ErrorResponseBuilder(props, "profile-service");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(builder);
        ProfileAvatarController controller = new ProfileAvatarController(profileCommands, profileQueries);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requestAvatarUploadUrl_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_CUSTOMER"));
        PresignedUrl presignedUrl = PresignedUrl.builder()
                .url("http://minio/upload")
                .method("PUT")
                .expiresAt(Instant.now().plusSeconds(300))
                .headers(Map.of("Content-Type", "image/png"))
                .objectKey("user-avatars/key")
                .build();
        when(profileCommands.prepareAvatarUpload(any(), any())).thenReturn(presignedUrl);

        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/api/profiles/me/avatar-upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(new com.example.profile_service.web.model.AvatarUploadRequest()
                                .fileName("avatar.png")
                                .contentType("image/png"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("PUT"))
                .andExpect(jsonPath("$.objectKey").value("user-avatars/key"));
    }

    @Test
    void getAvatarViewUrl_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_CUSTOMER"));
        PresignedUrl presignedUrl = PresignedUrl.builder()
                .url("http://minio/view")
                .method("GET")
                .expiresAt(Instant.now().plusSeconds(300))
                .headers(Map.of())
                .objectKey("user-avatars/key")
                .build();
        when(profileQueries.getAvatarView(userId)).thenReturn(presignedUrl);

        mvc.perform(get("/api/profiles/me/avatar-view-url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://minio/view"));
    }

    private void setAuthentication(UUID subject, List<String> authorities) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject.toString())
                .claim("sub", subject.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        List<SimpleGrantedAuthority> granted = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        Authentication auth = new JwtAuthenticationToken(jwt, granted);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
