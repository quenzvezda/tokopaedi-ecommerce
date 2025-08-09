package com.example.iam.domain.port;

import com.example.iam.domain.model.Role;
import java.util.*;

public interface RolePort {
    Role save(Role r);
    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    void deleteById(Long id);
}
