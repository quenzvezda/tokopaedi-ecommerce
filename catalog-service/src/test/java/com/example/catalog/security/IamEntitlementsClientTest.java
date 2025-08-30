package com.example.catalog.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class IamEntitlementsClientTest {

    RestTemplate rt = new RestTemplate();
    IamClientProperties props = new IamClientProperties();
    IamEntitlementsClient client = new IamEntitlementsClient(rt, props);

    @Test
    void fetch_ok() {
        props.setInternalAuthValue("secret");
        MockRestServiceServer server = MockRestServiceServer.bindTo(rt).build();
        UUID id = UUID.randomUUID();
        server.expect(once(), requestTo("http://iam-service/internal/v1/entitlements/"+id))
                .andExpect(header("X-Internal-Token", "secret"))
                .andRespond(withSuccess("{\"perm_ver\":1,\"scopes\":[\"a\"]}", MediaType.APPLICATION_JSON));

        EntitlementsDto dto = client.fetch(id);
        assertThat(dto.perm_ver()).isEqualTo(1);
        server.verify();
    }

    @Test
    void fetch_error_returnsEmpty() {
        MockRestServiceServer server = MockRestServiceServer.bindTo(rt).build();
        UUID id = UUID.randomUUID();
        server.expect(once(), requestTo("http://iam-service/internal/v1/entitlements/"+id))
                .andRespond(withServerError());
        EntitlementsDto dto = client.fetch(id);
        assertThat(dto.scopes()).isEmpty();
    }
}

