package com.academy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration (placeholder)
 * Report: Security notes - Basic security config with authentication disabled for development
 * 
 * To enable OAuth2/JWT in production:
 * 1. Add spring-boot-starter-oauth2-resource-server dependency
 * 2. Configure JWT decoder with issuer URI
 * 3. Update SecurityFilterChain to require authentication for /api/** endpoints
 * 4. Add @PreAuthorize annotations to controllers for role-based access
 * 5. Configure CORS for frontend integration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}

