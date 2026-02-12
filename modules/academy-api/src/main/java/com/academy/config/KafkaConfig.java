package com.academy.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for API module
 * Creates Kafka topics for events
 */
@Configuration
public class KafkaConfig {
    
    public static final String STUDENT_REGISTERED_TOPIC = "student.registered";
    public static final String MENTOR_SESSION_CREATED_TOPIC = "mentor.session.created";
    public static final String BATCH_CREATED_TOPIC = "batch.created";
    
    @Bean
    public NewTopic studentRegisteredTopic() {
        return TopicBuilder.name(STUDENT_REGISTERED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic mentorSessionCreatedTopic() {
        return TopicBuilder.name(MENTOR_SESSION_CREATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic batchCreatedTopic() {
        return TopicBuilder.name(BATCH_CREATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
