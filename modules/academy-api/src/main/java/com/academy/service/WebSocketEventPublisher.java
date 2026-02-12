package com.academy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for publishing WebSocket events to connected clients
 * Publishes realtime updates for student and batch operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Publish student created event
     */
    public void publishStudentCreated(Long studentId, String email) {
        Map<String, Object> event = createEvent("STUDENT_CREATED", studentId, Map.of("email", email));
        messagingTemplate.convertAndSend("/topic/students", event);
        log.debug("Published STUDENT_CREATED event for student: {}", studentId);
    }

    /**
     * Publish student updated event
     */
    public void publishStudentUpdated(Long studentId, String email) {
        Map<String, Object> event = createEvent("STUDENT_UPDATED", studentId, Map.of("email", email));
        messagingTemplate.convertAndSend("/topic/students", event);
        log.debug("Published STUDENT_UPDATED event for student: {}", studentId);
    }

    /**
     * Publish student deleted event
     */
    public void publishStudentDeleted(Long studentId) {
        Map<String, Object> event = createEvent("STUDENT_DELETED", studentId, Map.of());
        messagingTemplate.convertAndSend("/topic/students", event);
        log.debug("Published STUDENT_DELETED event for student: {}", studentId);
    }

    /**
     * Publish batch created event
     */
    public void publishBatchCreated(Long batchId, String name) {
        Map<String, Object> event = createEvent("BATCH_CREATED", batchId, Map.of("name", name));
        messagingTemplate.convertAndSend("/topic/batches", event);
        log.debug("Published BATCH_CREATED event for batch: {}", batchId);
    }

    /**
     * Publish batch updated event
     */
    public void publishBatchUpdated(Long batchId, String name) {
        Map<String, Object> event = createEvent("BATCH_UPDATED", batchId, Map.of("name", name));
        messagingTemplate.convertAndSend("/topic/batches", event);
        log.debug("Published BATCH_UPDATED event for batch: {}", batchId);
    }

    /**
     * Publish batch deleted event
     */
    public void publishBatchDeleted(Long batchId) {
        Map<String, Object> event = createEvent("BATCH_DELETED", batchId, Map.of());
        messagingTemplate.convertAndSend("/topic/batches", event);
        log.debug("Published BATCH_DELETED event for batch: {}", batchId);
    }

    private Map<String, Object> createEvent(String type, Long id, Map<String, Object> additionalData) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", type);
        event.put("timestamp", Instant.now().toString());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.putAll(additionalData);
        event.put("payload", payload);
        
        return event;
    }
}

