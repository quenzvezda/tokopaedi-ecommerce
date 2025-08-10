package com.example.auth.domain.port;

import java.util.List;
import java.util.UUID;

public interface IamPort {
    int getPermVersion(UUID accountId);
    List<String> getUserRoles(UUID accountId);
}
