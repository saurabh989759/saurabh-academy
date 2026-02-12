package com.academy.config;

import com.academy.dto.ApiLogEntry;
import com.academy.service.ApiLoggingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter to intercept and log all API requests and responses
 * Captures request/response body, headers, status code, and timing
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ApiLoggingFilter extends OncePerRequestFilter {
    
    private final ApiLoggingService apiLoggingService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Skip logging for actuator endpoints and static resources
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || 
            path.startsWith("/swagger-ui") || 
            path.startsWith("/api-docs") ||
            path.startsWith("/v3/api-docs") ||
            path.equals("/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        // Wrap request and response to enable body reading
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Log after processing
            long duration = System.currentTimeMillis() - startTime;
            logApiCall(wrappedRequest, wrappedResponse, duration);
            
            // Copy response body back to original response
            wrappedResponse.copyBodyToResponse();
        }
    }
    
    private void logApiCall(ContentCachingRequestWrapper request, 
                           ContentCachingResponseWrapper response, 
                           long durationMs) {
        try {
            ApiLogEntry.ApiLogEntryBuilder builder = ApiLogEntry.builder()
                .timestamp(LocalDateTime.now())
                .method(request.getMethod())
                .uri(request.getRequestURI())
                .path(request.getServletPath())
                .statusCode(response.getStatus())
                .durationMs(durationMs)
                .clientIp(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"));
            
            // Extract query parameters
            if (request.getQueryString() != null) {
                Map<String, String> queryParams = new HashMap<>();
                String[] params = request.getQueryString().split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=", 2);
                    if (keyValue.length == 2) {
                        queryParams.put(keyValue[0], keyValue[1]);
                    }
                }
                builder.queryParams(queryParams);
            }
            
            // Extract headers (exclude sensitive headers)
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!isSensitiveHeader(headerName)) {
                    headers.put(headerName, request.getHeader(headerName));
                }
            }
            builder.headers(headers);
            
            // Extract request body
            byte[] requestBodyBytes = request.getContentAsByteArray();
            if (requestBodyBytes.length > 0) {
                String requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
                // Limit request body size to prevent huge logs
                if (requestBody.length() > 10000) {
                    requestBody = requestBody.substring(0, 10000) + "... [truncated]";
                }
                builder.requestBody(requestBody);
            }
            
            // Extract response body
            byte[] responseBodyBytes = response.getContentAsByteArray();
            if (responseBodyBytes.length > 0) {
                String responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);
                // Limit response body size to prevent huge logs
                if (responseBody.length() > 10000) {
                    responseBody = responseBody.substring(0, 10000) + "... [truncated]";
                }
                builder.responseBody(responseBody);
            }
            
            ApiLogEntry logEntry = builder.build();
            apiLoggingService.logApiCall(logEntry);
            
        } catch (Exception e) {
            log.error("Error logging API call", e);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    private boolean isSensitiveHeader(String headerName) {
        String lower = headerName.toLowerCase();
        return lower.contains("authorization") || 
               lower.contains("cookie") || 
               lower.contains("password") ||
               lower.contains("token");
    }
}

