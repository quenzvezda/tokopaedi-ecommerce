package com.example.profile.web.store;

import com.example.profile.application.profile.ProfileCommands;
import com.example.profile.application.profile.ProfileQueries;
import com.example.profile.domain.store.StoreProfile;
import com.example.profile.web.GlobalExceptionHandler;
import com.example.profile_service.web.model.StoreCreateRequest;
import com.example.profile_service.web.model.StoreUpdateRequest;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfileStoreControllerTest {

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
        ProfileStoreController controller = new ProfileStoreController(profileCommands, profileQueries);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listMyStores_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_SELLER"));
        StoreProfile store = StoreProfile.builder()
                .id(UUID.randomUUID())
                .ownerId(userId)
                .name("My Store")
                .slug("my-store")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(profileQueries.listStores(userId)).thenReturn(List.of(store));

        mvc.perform(get("/api/profiles/me/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("My Store"));
    }

    @Test
    void createStore_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_CUSTOMER"));
        StoreProfile store = StoreProfile.builder()
                .id(UUID.randomUUID())
                .ownerId(userId)
                .name("New Store")
                .slug("new-store")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        lenient().when(profileCommands.createStore(eq(userId), any())).thenReturn(store);

        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/api/profiles/me/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(new StoreCreateRequest()
                                .name("New Store")
                                .slug("new-store"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Store"));
    }

    @Test
    void updateStore_ok() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        setAuthentication(userId, List.of("ROLE_SELLER"));
        StoreProfile store = StoreProfile.builder()
                .id(storeId)
                .ownerId(userId)
                .name("Updated Store")
                .slug("updated-store")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        lenient().when(profileCommands.updateStore(eq(userId), eq(storeId), any())).thenReturn(store);

        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(patch("/api/profiles/me/stores/" + storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(new StoreUpdateRequest()
                                .name("Updated Store"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Store"));

        verify(profileCommands).updateStore(eq(userId), eq(storeId), any());
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
