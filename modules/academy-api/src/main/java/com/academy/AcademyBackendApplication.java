package com.academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Academy Backend Application
 * Main API module - handles REST endpoints and API requests
 */
@SpringBootApplication(scanBasePackages = "com.academy")
@EntityScan("com.academy.entity")
@EnableJpaRepositories("com.academy.repository")
@EnableCaching
@EnableKafka
@EnableScheduling
public class AcademyBackendApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AcademyBackendApplication.class, args);
    }
}
