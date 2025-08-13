package com.example.iam.domain.port;

import com.example.iam.domain.model.UserRole;
import java.util.*;

public interface UserRolePort {
    boolean exists(UUID accountId, Long roleId);
    void add(UUID accountId, Long roleId);
    void remove(UUID accountId, Long roleId);
    List<UserRole> findByAccountId(UUID accountId);
    List<UserRole> findByRoleId(Long roleId);
    List<Long> findRoleIdsByAccountId(UUID accountId);
}
