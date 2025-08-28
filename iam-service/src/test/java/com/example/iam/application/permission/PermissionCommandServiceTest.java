package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PermissionCommandServiceTest {

    PermissionRepository repo = mock(PermissionRepository.class);
    PermissionCommands svc = new PermissionCommandService(repo);

    @Test
    void create_savesOfNew() {
        when(repo.save(any())).thenReturn(new Permission(1L, "A", "d"));
        var out = svc.create("A", "d");
        var captor = ArgumentCaptor.forClass(Permission.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getName()).isEqualTo("A");
        assertThat(out.getId()).isEqualTo(1L);
    }

    @Test
    void update_loads_thenSavesMutated() {
        when(repo.findById(7L)).thenReturn(Optional.of(new Permission(7L, "X", "Y")));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var out = svc.update(7L, "N", "D");

        assertThat(out.getName()).isEqualTo("N");
        assertThat(out.getDescription()).isEqualTo("D");
        verify(repo).save(new Permission(7L, "N", "D"));
    }

    @Test
    void update_throwsWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> svc.update(99L, "N", "D"));
        verify(repo, never()).save(any());
    }

    @Test
    void delete_callsRepo() {
        svc.delete(1L);
        verify(repo).deleteById(1L);
    }
}
