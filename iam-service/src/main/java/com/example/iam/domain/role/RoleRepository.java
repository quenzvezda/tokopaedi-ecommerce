package com.example.iam.domain.role;

import java.util.*;

public interface RoleRepository {
    Role save(Role r);
    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    void deleteById(Long id);
}
