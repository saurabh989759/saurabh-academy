package com.academy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0.1 configuration
 * Report: API Documentation - OpenAPI 3.0.1 specification implementation
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .openapi("3.0.1")
            .info(new Info()
                .title("Academy Management System API")
                .version("1.0.0")
                .description("""
                    REST API for Academy Management System. This API provides endpoints for managing students, 
                    batches, classes, mentors, and mentor sessions.
                    
                    The API supports:
                    - Student registration and management
                    - Batch creation and management
                    - Class scheduling and management
                    - Mentor management
                    - Mentor session booking and management
                    
                    All create operations publish Kafka events for event-driven architecture.
                    """)
                .contact(new Contact()
                    .name("Academy Development Team")
                    .email("support@academy.com")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development server"),
                new Server()
                    .url("https://academy-backend-dev.example.com")
                    .description("Development environment"),
                new Server()
                    .url("https://academy-backend.example.com")
                    .description("Production environment")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token authentication")));
    }
}

