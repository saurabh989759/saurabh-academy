package com.academy.kafka.producer;

import com.academy.dto.EventDTO;
import com.academy.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer for student events
 * Module: academy-kafka-producer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentEventProducer {
    
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    
    @Value("${kafka.topics.student-registered:student.registered}")
    private String studentRegisteredTopic;
    
    public void publishStudentRegisteredEvent(Student student) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("studentId", student.getId());
        payload.put("name", student.getName());
        payload.put("email", student.getEmail());
        payload.put("batchId", student.getBatch() != null ? student.getBatch().getId() : null);
        
        EventDTO event = new EventDTO();
        event.setEventType("student.registered");
        event.setTimestamp(Instant.now());
        event.setPayload(payload);
        
        try {
            kafkaTemplate.send(studentRegisteredTopic, event);
            log.info("Published student.registered event for student: {}", student.getId());
        } catch (Exception e) {
            log.error("Error publishing student.registered event", e);
            throw new RuntimeException("Failed to publish student.registered event", e);
        }
    }
}

