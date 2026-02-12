package com.academy.kafka;

import com.academy.config.KafkaConfig;
import com.academy.dto.EventDTO;
import com.academy.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer for student events
 * Report: Kafka Integration - Producer for student.registered events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentEventProducer {
    
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    
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
            kafkaTemplate.send(KafkaConfig.STUDENT_REGISTERED_TOPIC, event);
            log.info("Published student.registered event for student: {}", student.getId());
        } catch (Exception e) {
            log.error("Error publishing student.registered event", e);
        }
    }
}

