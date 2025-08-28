package com.example.iam.application.role;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RoleCommandServiceTest {

    RoleRepository repo = mock(RoleRepository.class);
    RoleCommands svc = new RoleCommandService(repo);

    @Test
    void create_savesNewRole() {
        when(repo.save(any())).thenReturn(new Role(1L,"ADMIN"));
        var out = svc.create("ADMIN");
        verify(repo).save(Role.ofNew("ADMIN"));
        assertThat(out.getId()).isEqualTo(1L);
    }

    @Test
    void update_mutatesName() {
        when(repo.findById(5L)).thenReturn(Optional.of(new Role(5L,"A")));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var out = svc.update(5L, "B");

        assertThat(out.getName()).isEqualTo("B");
        verify(repo).save(new Role(5L,"B"));
    }

    @Test
    void update_missing_throws() {
        when(repo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> svc.update(5L, "B"));
    }

    @Test
    void delete_callsRepo() {
        svc.delete(3L);
        verify(repo).deleteById(3L);
    }
}
