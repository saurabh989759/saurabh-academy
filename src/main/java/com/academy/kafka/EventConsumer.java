package com.academy.kafka;

import com.academy.config.KafkaConfig;
import com.academy.dto.EventDTO;
import com.academy.entity.AuditEvent;
import com.academy.repository.AuditEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Kafka consumer for domain events
 * Report: Kafka Integration - Consumer that processes events and stores in audit_events table
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    
    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(topics = KafkaConfig.STUDENT_REGISTERED_TOPIC, groupId = "academy-backend-group")
    public void consumeStudentRegistered(EventDTO event) {
        log.info("Consumed student.registered event: {}", event);
        saveAuditEvent(event);
    }
    
    @KafkaListener(topics = KafkaConfig.MENTOR_SESSION_CREATED_TOPIC, groupId = "academy-backend-group")
    public void consumeMentorSessionCreated(EventDTO event) {
        log.info("Consumed mentor.session.created event: {}", event);
        saveAuditEvent(event);
    }
    
    @KafkaListener(topics = KafkaConfig.BATCH_CREATED_TOPIC, groupId = "academy-backend-group")
    public void consumeBatchCreated(EventDTO event) {
        log.info("Consumed batch.created event: {}", event);
        saveAuditEvent(event);
    }
    
    private void saveAuditEvent(EventDTO event) {
        try {
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setEventType(event.getEventType());
            auditEvent.setPayload(objectMapper.writeValueAsString(event));
            auditEvent.setCreatedAt(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());
            
            auditEventRepository.save(auditEvent);
            log.info("Saved audit event: {}", event.getEventType());
        } catch (JsonProcessingException e) {
            log.error("Error saving audit event", e);
        }
    }
}

