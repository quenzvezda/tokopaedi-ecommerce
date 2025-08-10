package com.example.auth.infrastructure.kafka;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter @Setter
public class IamPermissionEvent {
    private String account_id;
    private Integer perm_ver;
    private List<String> roles;
    private String ts;

    public Instant eventTime() { return ts == null ? Instant.now() : Instant.parse(ts); }
}
