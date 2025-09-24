package com.example.profile.infrastructure.iam;

import com.example.common.web.error.ApiException;
import com.example.profile.config.ProfileIamProperties;
import com.example.profile.domain.profile.SellerRoleGateway;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class IamSellerRoleGateway implements SellerRoleGateway {

    private static final Logger log = LoggerFactory.getLogger(IamSellerRoleGateway.class);

    private final RestTemplate restTemplate;
    private final ProfileIamProperties properties;
    private final AtomicReference<Long> cachedSellerRoleId = new AtomicReference<>();

    @Override
    public void ensureSellerRole(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId");
        try {
            List<String> roles = fetchCurrentRoles(accountId);
            if (roles.stream().anyMatch(role -> role.equalsIgnoreCase(properties.getSellerRoleName()))) {
                return;
            }
            Long roleId = resolveSellerRoleId();
            if (roleId == null) {
                throw ApiException.serviceUnavailable("iam_role_missing", "Seller role not found in IAM");
            }
            assignRole(accountId, roleId);
        } catch (ApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("IAM request failed: {}", ex.toString());
            throw ApiException.serviceUnavailable("iam_unavailable", "IAM service unavailable");
        }
    }

    private List<String> fetchCurrentRoles(UUID accountId) {
        String url = properties.getBaseUrl() + properties.getRolesPath();
        HttpHeaders headers = buildHeaders();
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                Map.of("accountId", accountId.toString())
        );
        return response.getBody() != null ? response.getBody() : List.of();
    }

    private Long resolveSellerRoleId() {
        Long configured = properties.getSellerRoleId();
        if (configured != null) {
            cachedSellerRoleId.compareAndSet(null, configured);
            return configured;
        }
        Long cached = cachedSellerRoleId.get();
        if (cached != null) {
            return cached;
        }
        String url = properties.getBaseUrl() + properties.getListRolesPath();
        HttpHeaders headers = buildHeaders();
        ResponseEntity<List<IamRoleDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        List<IamRoleDto> roles = response.getBody();
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .filter(role -> role.getName() != null && role.getName().equalsIgnoreCase(properties.getSellerRoleName()))
                .map(IamRoleDto::getId)
                .filter(Objects::nonNull)
                .findFirst()
                .map(id -> {
                    cachedSellerRoleId.set(id);
                    return id;
                })
                .orElse(null);
    }

    private void assignRole(UUID accountId, Long roleId) {
        String url = properties.getBaseUrl() + properties.getAssignRolePath();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(null, headers),
                Void.class,
                Map.of("accountId", accountId.toString(), "roleId", roleId)
        );
        log.info("Assigned SELLER role {} to account {}", roleId, accountId);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (properties.getInternalAuthHeader() != null && properties.getInternalAuthValue() != null && !properties.getInternalAuthValue().isBlank()) {
            headers.add(properties.getInternalAuthHeader(), properties.getInternalAuthValue());
        }
        return headers;
    }
}
