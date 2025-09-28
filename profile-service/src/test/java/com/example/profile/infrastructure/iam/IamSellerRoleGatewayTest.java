package com.example.profile.infrastructure.iam;

import com.example.common.web.error.ApiException;
import com.example.profile.config.ProfileIamProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IamSellerRoleGatewayTest {

    private RestTemplate restTemplate;
    private ProfileIamProperties properties;
    private IamSellerRoleGateway gateway;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        properties = new ProfileIamProperties();
        properties.setBaseUrl("http://iam");
        properties.setInternalAuthValue("secret");
        gateway = new IamSellerRoleGateway(restTemplate, properties);
    }

    @Test
    void ensureSellerRole_alreadyAssigned_skipsAssignment() {
        String rolesUrl = properties.getBaseUrl() + properties.getRolesPath();
        when(restTemplate.exchange(eq(rolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class), anyMap()))
                .thenReturn(ResponseEntity.ok(List.of("seller")));

        gateway.ensureSellerRole(UUID.randomUUID());

        verify(restTemplate, never()).exchange(eq(properties.getBaseUrl() + properties.getAssignRolePath()), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class), anyMap());
    }

    @Test
    void ensureSellerRole_withConfiguredRoleId_assignsRole() {
        properties.setSellerRoleId(42L);
        String rolesUrl = properties.getBaseUrl() + properties.getRolesPath();
        String assignUrl = properties.getBaseUrl() + properties.getAssignRolePath();
        when(restTemplate.exchange(eq(rolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class), anyMap()))
                .thenReturn(ResponseEntity.ok(List.of()));
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(eq(assignUrl), eq(HttpMethod.POST), entityCaptor.capture(), eq(Void.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        gateway.ensureSellerRole(UUID.randomUUID());

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertThat(headers.getFirst(properties.getInternalAuthHeader())).isEqualTo("secret");
        assertThat(headers.getAccept()).contains(MediaType.APPLICATION_JSON);
    }

    @Test
    void ensureSellerRole_fetchesRoleFromIamWhenNotConfigured() {
        String rolesUrl = properties.getBaseUrl() + properties.getRolesPath();
        String listRolesUrl = properties.getBaseUrl() + properties.getListRolesPath();
        String assignUrl = properties.getBaseUrl() + properties.getAssignRolePath();
        when(restTemplate.exchange(eq(rolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class), anyMap()))
                .thenReturn(ResponseEntity.ok(List.of()));
        IamRoleDto roleDto = new IamRoleDto();
        roleDto.setId(88L);
        roleDto.setName("SELLER");
        when(restTemplate.exchange(eq(listRolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(roleDto)));
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        when(restTemplate.exchange(eq(assignUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class), varsCaptor.capture()))
                .thenReturn(ResponseEntity.ok().build());

        UUID accountId = UUID.randomUUID();
        gateway.ensureSellerRole(accountId);

        assertThat(varsCaptor.getValue().get("roleId")).isEqualTo(88L);
        assertThat(varsCaptor.getValue().get("accountId")).isEqualTo(accountId.toString());
        verify(restTemplate).exchange(eq(listRolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class));

        clearInvocations(restTemplate);
        when(restTemplate.exchange(eq(rolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class), anyMap()))
                .thenReturn(ResponseEntity.ok(List.of()));

        gateway.ensureSellerRole(UUID.randomUUID());

        verify(restTemplate, never()).exchange(eq(listRolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }

    @Test
    void ensureSellerRole_roleMissingThrowsServiceUnavailable() {
        String rolesUrl = properties.getBaseUrl() + properties.getRolesPath();
        String listRolesUrl = properties.getBaseUrl() + properties.getListRolesPath();
        when(restTemplate.exchange(eq(rolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class), anyMap()))
                .thenReturn(ResponseEntity.ok(List.of()));
        when(restTemplate.exchange(eq(listRolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        assertThatThrownBy(() -> gateway.ensureSellerRole(UUID.randomUUID()))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("iam_role_missing");
    }

    @Test
    void ensureSellerRole_restClientFailureWrapped() {
        String rolesUrl = properties.getBaseUrl() + properties.getRolesPath();
        when(restTemplate.exchange(eq(rolesUrl), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class), anyMap()))
                .thenThrow(new RestClientException("down"));

        assertThatThrownBy(() -> gateway.ensureSellerRole(UUID.randomUUID()))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("iam_unavailable");
    }
}
