package com.academy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
            .openapi("3.0.1")
            .info(apiInfo())
            .servers(serverList())
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components().addSecuritySchemes("bearerAuth", jwtScheme()));
    }

    private Info apiInfo() {
        return new Info()
            .title("Academy Management System API")
            .version("1.0.0")
            .description("REST API for managing students, batches, classes, mentors, and sessions.")
            .contact(new Contact().name("Academy Dev Team").email("support@academy.com"));
    }

    private List<Server> serverList() {
        return List.of(
            new Server().url("http://localhost:8080").description("Local"),
            new Server().url("https://academy-backend-dev.example.com").description("Dev"),
            new Server().url("https://academy-backend.example.com").description("Production")
        );
    }

    private SecurityScheme jwtScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
    }
}
