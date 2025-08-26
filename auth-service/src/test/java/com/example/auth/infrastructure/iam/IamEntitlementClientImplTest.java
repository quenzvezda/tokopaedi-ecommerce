package com.example.auth.infrastructure.iam;

import com.example.auth.domain.entitlement.Entitlements;
import com.example.auth.web.error.IamUnavailableException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

class IamEntitlementClientImplTest {

	WireMockServer wm;
	IamEntitlementClientImpl client;

	@BeforeEach
	void setup() {
		wm = new WireMockServer(0);
		wm.start();
		configureFor("localhost", wm.port());

		WebClient webClient = WebClient.builder()
				.baseUrl("http://localhost:" + wm.port())
				.build();

		client = new IamEntitlementClientImpl(webClient, 1000, "SVC-TOKEN");
	}

	@AfterEach
	void tearDown() { wm.stop(); }

	@Test
	void fetchEntitlements_success_mergesScopesAndRoles() {
		UUID id = UUID.randomUUID();

		stubFor(get(urlEqualTo("/internal/v1/entitlements/" + id))
				.withHeader("X-Internal-Token", equalTo("SVC-TOKEN"))
				.willReturn(okJson("{\"perm_ver\":4,\"scopes\":[\"product:brand:read\"]}")));

		stubFor(get(urlEqualTo("/internal/v1/users/" + id + "/roles"))
				.withHeader("X-Internal-Token", equalTo("SVC-TOKEN"))
				.willReturn(okJson("[\"ADMIN\"]")));

		Entitlements ent = client.fetchEntitlements(id);

		assertThat(ent.getPermVer()).isEqualTo(4);
		assertThat(ent.getRoles()).isEqualTo(List.of("ADMIN"));
	}

	@Test
	void fetchEntitlements_upstream4xx_throwsServiceUnavailableWithReason() {
		UUID id = UUID.randomUUID();

		stubFor(get(urlMatching("/internal/v1/.*"))
				.willReturn(aResponse().withStatus(404)));

		assertThatThrownBy(() -> client.fetchEntitlements(id))
				.isInstanceOf(IamUnavailableException.class)
				.hasMessageContaining("iam_unavailable");
	}

    @Test
    void fetchEntitlements_upstream5xx_mapsToUpstream5xx() {
        UUID id = UUID.randomUUID();

        stubFor(get(urlMatching("/internal/v1/.*"))
                .willReturn(aResponse().withStatus(500)));

        Throwable thrown = catchThrowable(() -> client.fetchEntitlements(id));

        assertThat(thrown).isInstanceOf(IamUnavailableException.class);

        // cast ke ApiException untuk cek code & meta
        var api = (com.example.common.web.error.ApiException) thrown;
        assertThat(api.code()).isEqualTo("iam_unavailable");
        assertThat(api.meta()).containsEntry("reason", "upstream_5xx");
    }

    @Test
    void fetchEntitlements_timeout_mapsToUnknown() {
        UUID id = UUID.randomUUID();

        // delay di atas timeout client (client di-construct dengan responseMs = 1000)
        stubFor(get(urlPathMatching("/internal/v1/entitlements/.*"))
                .willReturn(okJson("{\"perm_ver\":1,\"scopes\":[]}").withFixedDelay(1500)));
        stubFor(get(urlPathMatching("/internal/v1/users/.*/roles"))
                .willReturn(okJson("[]").withFixedDelay(1500)));

        Throwable thrown = catchThrowable(() -> client.fetchEntitlements(id));

        assertThat(thrown).isInstanceOf(IamUnavailableException.class);

        var api = (com.example.common.web.error.ApiException) thrown;
        assertThat(api.code()).isEqualTo("iam_unavailable");
        assertThat(api.meta()).containsEntry("reason", "unknown");
    }
}
