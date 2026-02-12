package com.academy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for API request/response logging
 * Stores complete request and response information in structured format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiLogEntry {
    private String id;
    private LocalDateTime timestamp;
    private String method;
    private String uri;
    private String path;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String requestBody;
    private Integer statusCode;
    private String responseBody;
    private Long durationMs;
    private String clientIp;
    private String userAgent;
}

