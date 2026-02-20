package com.academy.kafka.producer;

import com.academy.dto.EventDTO;
import com.academy.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentEventProducer {

    private final KafkaTemplate<String, EventDTO> kafkaTemplate;

    @Value("${kafka.topics.student-registered:student.registered}")
    private String topic;

    public void publishStudentRegisteredEvent(Student student) {
        EventDTO event = buildEvent("student.registered", Map.of(
            "studentId", student.getId(),
            "name", student.getName(),
            "email", student.getEmail(),
            "batchId", student.getBatch() != null ? student.getBatch().getId() : null
        ));

        dispatch(topic, event, student.getId());
    }

    // -------------------------------------------------------------------------

    private EventDTO buildEvent(String type, Map<String, Object> payload) {
        EventDTO event = new EventDTO();
        event.setEventType(type);
        event.setTimestamp(Instant.now());
        event.setPayload(payload);
        return event;
    }

    private void dispatch(String destination, EventDTO event, Object entityId) {
        try {
            kafkaTemplate.send(destination, event);
            log.info("Event '{}' dispatched for entity id={}", event.getEventType(), entityId);
        } catch (Exception ex) {
            log.error("Failed to dispatch event '{}': {}", event.getEventType(), ex.getMessage());
            throw new RuntimeException("Event dispatch failed: " + event.getEventType(), ex);
        }
    }
}
