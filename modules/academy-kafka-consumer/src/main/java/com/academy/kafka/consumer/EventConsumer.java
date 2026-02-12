package com.academy.kafka.consumer;

import com.academy.dto.EventDTO;
import com.academy.entity.AuditEvent;
import com.academy.repository.AuditEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Kafka consumer for domain events with retry mechanism
 * Module: academy-kafka-consumer
 * 
 * Features:
 * - Retry mechanism with exponential backoff
 * - Manual acknowledgment for better error handling
 * - Transaction support for database operations
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    
    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(
        topics = "${kafka.topics.student-registered:student.registered}",
        groupId = "${kafka.consumer.group-id:academy-kafka-consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public void consumeStudentRegistered(
            @Payload EventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Consuming student.registered event from topic: {}, partition: {}, offset: {}", 
                topic, partition, offset);
        
        try {
            saveAuditEvent(event);
            log.info("Successfully processed student.registered event: {}", event.getEventType());
            
            // Acknowledge only after successful processing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.error("Error processing student.registered event. Will retry. Event: {}", event, e);
            throw e; // Re-throw to trigger retry
        }
    }
    
    @KafkaListener(
        topics = "${kafka.topics.mentor-session-created:mentor.session.created}",
        groupId = "${kafka.consumer.group-id:academy-kafka-consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public void consumeMentorSessionCreated(
            @Payload EventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Consuming mentor.session.created event from topic: {}, partition: {}, offset: {}", 
                topic, partition, offset);
        
        try {
            saveAuditEvent(event);
            log.info("Successfully processed mentor.session.created event: {}", event.getEventType());
            
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.error("Error processing mentor.session.created event. Will retry. Event: {}", event, e);
            throw e;
        }
    }
    
    @KafkaListener(
        topics = "${kafka.topics.batch-created:batch.created}",
        groupId = "${kafka.consumer.group-id:academy-kafka-consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public void consumeBatchCreated(
            @Payload EventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Consuming batch.created event from topic: {}, partition: {}, offset: {}", 
                topic, partition, offset);
        
        try {
            saveAuditEvent(event);
            log.info("Successfully processed batch.created event: {}", event.getEventType());
            
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.error("Error processing batch.created event. Will retry. Event: {}", event, e);
            throw e;
        }
    }
    
    private void saveAuditEvent(EventDTO event) {
        try {
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setEventType(event.getEventType());
            auditEvent.setPayload(objectMapper.writeValueAsString(event));
            auditEvent.setCreatedAt(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());
            
            auditEventRepository.save(auditEvent);
            log.debug("Saved audit event: {}", event.getEventType());
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON", e);
            throw new RuntimeException("Failed to save audit event", e);
        }
    }
}

