package com.example.auth.testutil;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.entitlement.Entitlements;
import com.example.auth.domain.token.RefreshToken;

import java.time.*;
import java.util.List;
import java.util.UUID;

public final class TestFixtures {
	private TestFixtures(){}

	public static Account account(UUID id, String uname, String email, String hash) {
		return Account.of(id, uname, email, hash, "ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
		// TODO: sesuaikan dengan domain kalau factory-nya beda
	}

	public static Entitlements ent(UUID accountId, int ver, List<String> roles) {
		return Entitlements.of(accountId, ver, roles, Instant.now());
	}

	public static RefreshToken refresh(UUID id, UUID accountId, boolean revoked) {
		return RefreshToken.of(id, accountId, OffsetDateTime.now(ZoneOffset.UTC).plusDays(7), revoked);
	}
}
