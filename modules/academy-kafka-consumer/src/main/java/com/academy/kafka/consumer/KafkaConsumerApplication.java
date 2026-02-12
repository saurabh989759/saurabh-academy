package com.academy.kafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Kafka Consumer Application
 * Separate module for consuming Kafka events with retry mechanism
 */
@SpringBootApplication(scanBasePackages = "com.academy")
@EntityScan("com.academy.entity")
@EnableJpaRepositories("com.academy.repository")
public class KafkaConsumerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApplication.class, args);
    }
}

