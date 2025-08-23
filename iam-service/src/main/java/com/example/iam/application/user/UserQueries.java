package com.example.iam.application.user;

import java.util.List;
import java.util.UUID;

public interface UserQueries {
    List<String> getUserRoleNames(UUID accountId);
}
