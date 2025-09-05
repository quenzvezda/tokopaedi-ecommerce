package com.example.auth.web;

import com.example.auth.application.account.AccountCommands;
import com.example.auth.application.auth.AuthCommands;
import com.example.auth.application.auth.AuthCommands.TokenPair;
import com.example.auth.config.CommonWebConfig;
import com.example.auth.web.dto.LoginRequest;
import com.example.auth.web.dto.RefreshRequest;
import com.example.auth.web.dto.RegisterRequest;
import com.example.auth.web.error.InvalidCredentialsException;
import com.example.auth.web.error.RefreshTokenInvalidException;
import com.example.auth.web.error.UsernameAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CommonWebConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Resource MockMvc mvc;
    @Resource ObjectMapper om;

    @MockBean AuthCommands authCommands;
    @MockBean AccountCommands accountCommands;
    @MockBean com.example.auth.config.JwtSettings jwtSettings;

    /* ===================== REGISTER ===================== */

    @Test
    void register_success_returns201Created() throws Exception {
        var req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("a@x.io");
        req.setPassword("secret");

        UUID fakeId = UUID.randomUUID();
        when(accountCommands.register("alice","a@x.io","secret"))
                .thenReturn(fakeId);

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/accounts/" + fakeId))
                .andExpect(jsonPath("$.message").value("registered"));
    }

    @Test
    void register_validationError_returns400WithUpstream() throws Exception {
        var req = new RegisterRequest();
        req.setUsername("ab"); // invalid (min 3)
        req.setEmail("bad");
        req.setPassword("123");

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:validation"))
                .andExpect(jsonPath("$.upstream.errors", hasSize(greaterThan(0))));
    }

    @Test
    void register_conflictFromService_returns409() throws Exception {
        var req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("a@x.io");
        req.setPassword("secret");

        when(accountCommands.register("alice","a@x.io","secret"))
                .thenThrow(new UsernameAlreadyExistsException());

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("username_taken"));
    }

    @Test
    void register_malformedJson_returns400_badJson() throws Exception {
        String badJson = "{\"username\":\"ab\""; // sengaja tidak ditutup
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:malformed_json"));
    }

    /* ======================= LOGIN ====================== */

    @Test
    void login_success_returnsTokenPair() throws Exception {
        var body = new LoginRequest();
        body.setUsernameOrEmail("alice");
        body.setPassword("secret");

        when(authCommands.login("alice","secret"))
                .thenReturn(new TokenPair("Bearer","jwt",900,"rt"));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("jwt"))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("Pragma", org.hamcrest.Matchers.containsString("no-cache")))
                .andExpect(header().string("Expires", org.hamcrest.Matchers.containsString("0")));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        var body = new LoginRequest();
        body.setUsernameOrEmail("alice");
        body.setPassword("bad");

        when(authCommands.login("alice","bad"))
                .thenThrow(new InvalidCredentialsException());

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("invalid_credentials"));
    }

    @Test
    void login_methodNotAllowed_GET_returns405() throws Exception {
        mvc.perform(get("/api/v1/auth/login"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("method_not_allowed"))
                .andExpect(jsonPath("$.upstream.supported", notNullValue()));
    }

    @Test
    void login_unsupportedMediaType_returns415() throws Exception {
        var body = new LoginRequest();
        body.setUsernameOrEmail("alice"); body.setPassword("secret");

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("unsupported_media_type"))
                .andExpect(jsonPath("$.upstream.supported", notNullValue()));
    }

    /* ====================== REFRESH ===================== */

    @Test
    void refresh_success_setsCookie_andReturnsAccessTokenOnly() throws Exception {
        when(jwtSettings.getRefreshTtl()).thenReturn("PT30D");
        when(authCommands.refresh("rt"))
                .thenReturn(new TokenPair("Bearer", "jwt2", 900, "rt2"));

        mvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "rt")))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_token=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("Pragma", org.hamcrest.Matchers.containsString("no-cache")))
                .andExpect(header().string("Expires", org.hamcrest.Matchers.containsString("0")))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("jwt2"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }

    @Test
    void refresh_invalidToken_returns401() throws Exception {
        mvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("invalid_refresh_token"));
    }

    /* ====================== LOGOUT ===================== */

    @Test
    void logout_withCookie_revokesAndClearsCookie_204() throws Exception {
        when(jwtSettings.getRefreshTtl()).thenReturn("PT30D");

        mvc.perform(post("/api/v1/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "rt")))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_token=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("Pragma", org.hamcrest.Matchers.containsString("no-cache")))
                .andExpect(header().string("Expires", org.hamcrest.Matchers.containsString("0")));

        org.mockito.Mockito.verify(authCommands).logout("rt");
    }

    @Test
    void logout_withoutCookie_onlyClearsCookie_204() throws Exception {
        when(jwtSettings.getRefreshTtl()).thenReturn("PT30D");

        mvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_token=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("Pragma", org.hamcrest.Matchers.containsString("no-cache")))
                .andExpect(header().string("Expires", org.hamcrest.Matchers.containsString("0")));

        org.mockito.Mockito.verify(authCommands, org.mockito.Mockito.never()).logout(org.mockito.ArgumentMatchers.anyString());
    }
}
