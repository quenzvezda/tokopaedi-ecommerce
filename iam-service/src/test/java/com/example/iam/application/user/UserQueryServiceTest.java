package com.example.iam.application.user;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.user.UserRoleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserQueryServiceTest {

    UserRoleRepository userRole = mock(UserRoleRepository.class);
    RoleRepository role = mock(RoleRepository.class);
    UserQueries svc = new UserQueryService(userRole, role);

    @Test
    void noRoles_returnsEmpty() {
        var acc = UUID.randomUUID();
        when(userRole.findByAccountId(acc)).thenReturn(List.of());
        assertThat(svc.getUserRoleNames(acc)).isEmpty();
        verify(role, never()).findAll();
    }

    @Test
    void returnsTrimmedSortedNames_filteredByIds() {
        var acc = UUID.randomUUID();
        when(userRole.findByAccountId(acc)).thenReturn(List.of(
                UserRole.of(acc, 1L), UserRole.of(acc, 3L)));
        when(role.findAll()).thenReturn(List.of(
                new Role(1L, "  ADMIN "),
                new Role(2L, "IGNORED"),
                new Role(3L, " user "),
                new Role(4L, " ") // tidak akan terpilih
        ));

        var names = svc.getUserRoleNames(acc);

        assertThat(names).containsExactly("ADMIN", "user"); // sorted secara natural (A dulu)
    }
}
