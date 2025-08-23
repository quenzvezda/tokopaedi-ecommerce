package com.example.catalog.security;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * Client IAM untuk mengambil entitlements.
 */
@Component
@RequiredArgsConstructor
public class IamEntitlementsClient {

    private final @LoadBalanced RestTemplate lbRestTemplate;
    private final IamClientProperties props;

    public EntitlementsDto fetch(UUID accountId) {
        String url = props.getBaseUrl() + props.getEntitlementsPath();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        if (props.getInternalAuthHeader() != null && props.getInternalAuthValue() != null) {
            headers.set(props.getInternalAuthHeader(), props.getInternalAuthValue());
        }

        try {
            ResponseEntity<EntitlementsDto> resp = lbRestTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers),
                    EntitlementsDto.class, Map.of("accountId", accountId.toString())
            );
            return resp.getBody() != null ? resp.getBody() : new EntitlementsDto(null, java.util.List.of());
        } catch (RestClientException ex) {
            // Degrade gracefully: kembalikan entitlements kosong
            return new EntitlementsDto(null, java.util.List.of());
        }
    }
}
