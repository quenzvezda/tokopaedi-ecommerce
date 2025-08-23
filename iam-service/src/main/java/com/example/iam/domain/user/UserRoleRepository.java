package com.example.iam.domain.user;

import java.util.*;

public interface UserRoleRepository {
    boolean exists(UUID accountId, Long roleId);
    void add(UUID accountId, Long roleId);
    void remove(UUID accountId, Long roleId);
    List<UserRole> findByAccountId(UUID accountId);
    List<UserRole> findByRoleId(Long roleId);
    List<Long> findRoleIdsByAccountId(UUID accountId);
}
