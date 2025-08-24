package com.example.common.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    public Instant timestamp;
    public String service;
    public String path;
    public String method;
    public int status;
    public String code;
    public String message;
    public String requestId;
    public Map<String, Object> upstream;
    public String details;               // (dev only)
}
