package com.example.iam.application.entitlement;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.user.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EntitlementQueryServiceTest {

    UserRoleRepository userRole = mock(UserRoleRepository.class);
    RolePermissionRepository rolePerm = mock(RolePermissionRepository.class);
    PermissionRepository permission = mock(PermissionRepository.class);
    EntitlementVersionRepository version = mock(EntitlementVersionRepository.class);

    EntitlementQueries svc;

    @BeforeEach
    void setUp() { svc = new EntitlementQueryService(userRole, rolePerm, permission, version); }

    @Test
    void getEntitlements_whenNoRoles_returnsEmptyScopes_andPermVersion() {
        var acc = UUID.randomUUID();
        when(userRole.findByAccountId(acc)).thenReturn(List.of());
        when(version.getOrInit(acc)).thenReturn(5);

        var out = svc.getEntitlements(acc);

        assertThat(out.get("perm_ver")).isEqualTo(5);
        assertThat((List<?>) out.get("scopes")).isEmpty();
        verifyNoInteractions(permission);
        verify(rolePerm, never()).findPermissionIdsByRoleId(any());
    }

    @Test
    void getEntitlements_whenRolesButNoPerms_returnsEmptyScopes() {
        var acc = UUID.randomUUID();
        when(userRole.findByAccountId(acc)).thenReturn(List.of(
                UserRole.of(acc, 1L), UserRole.of(acc, 2L)));
        when(rolePerm.findPermissionIdsByRoleId(1L)).thenReturn(List.of());
        when(rolePerm.findPermissionIdsByRoleId(2L)).thenReturn(List.of());
        when(version.getOrInit(acc)).thenReturn(2);

        var out = svc.getEntitlements(acc);

        assertThat(out.get("perm_ver")).isEqualTo(2);
        assertThat((List<?>) out.get("scopes")).isEmpty();
    }

    @Test
    void getEntitlements_collectsDistinctTrimmedSortedScopes() {
        var acc = UUID.randomUUID();
        when(userRole.findByAccountId(acc)).thenReturn(List.of(UserRole.of(acc, 1L), UserRole.of(acc, 2L)));
        when(rolePerm.findPermissionIdsByRoleId(1L)).thenReturn(List.of(10L, 11L));
        when(rolePerm.findPermissionIdsByRoleId(2L)).thenReturn(List.of(11L, 12L)); // 11 duplikat
        when(permission.findAllByIds(new HashSet<>(Arrays.asList(10L,11L,12L))))
                .thenReturn(List.of(
                        new Permission(10L, " READ_USER ", "x"),
                        new Permission(11L, "READ_USER", "y"),     // duplikat nama
                        new Permission(12L, "  WRITE_USER", "z"),
                        new Permission(99L, "   ", "ignored")      // tidak mungkin muncul (id tak diminta), tapi aman
                ));
        when(version.getOrInit(acc)).thenReturn(9);

        var out = svc.getEntitlements(acc);

        assertThat(out.get("perm_ver")).isEqualTo(9);
        @SuppressWarnings("unchecked")
        var scopes = (List<String>) out.get("scopes");
        assertThat(scopes).containsExactly("READ_USER", "WRITE_USER"); // sorted & distinct & trimmed
    }

    @Test
    void checkAuthorization_returnsAllowOrDeny_andCarriesEntVersion() {
        var acc = UUID.randomUUID();
        when(userRole.findByAccountId(acc)).thenReturn(List.of(UserRole.of(acc, 1L)));
        when(rolePerm.findPermissionIdsByRoleId(1L)).thenReturn(List.of(10L));
        when(permission.findAllByIds(Set.of(10L))).thenReturn(List.of(new Permission(10L, "ORDER:READ", null)));
        when(version.getOrInit(acc)).thenReturn(3);

        var allow = svc.checkAuthorization(acc, "ORDER:READ");
        var deny  = svc.checkAuthorization(acc, "ORDER:WRITE");

        assertThat(allow.get("decision")).isEqualTo("ALLOW");
        assertThat(deny.get("decision")).isEqualTo("DENY");
        assertThat(allow.get("ent_v")).isEqualTo(3);
        assertThat(deny.get("ent_v")).isEqualTo(3);
    }
}
