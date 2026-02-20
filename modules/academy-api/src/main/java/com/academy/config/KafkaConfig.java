package com.academy.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    static final String TOPIC_STUDENT_REGISTERED    = "student.registered";
    static final String TOPIC_SESSION_CREATED       = "mentor.session.created";
    static final String TOPIC_BATCH_CREATED         = "batch.created";

    private static final int PARTITIONS = 3;
    private static final int REPLICAS   = 1;

    @Bean
    public NewTopic studentRegisteredTopic() {
        return TopicBuilder.name(TOPIC_STUDENT_REGISTERED)
            .partitions(PARTITIONS).replicas(REPLICAS).build();
    }

    @Bean
    public NewTopic mentorSessionCreatedTopic() {
        return TopicBuilder.name(TOPIC_SESSION_CREATED)
            .partitions(PARTITIONS).replicas(REPLICAS).build();
    }

    @Bean
    public NewTopic batchCreatedTopic() {
        return TopicBuilder.name(TOPIC_BATCH_CREATED)
            .partitions(PARTITIONS).replicas(REPLICAS).build();
    }
}
