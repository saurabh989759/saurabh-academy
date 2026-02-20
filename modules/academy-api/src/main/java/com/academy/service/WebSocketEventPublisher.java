package com.academy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventPublisher {

    private static final String STUDENTS_TOPIC = "/topic/students";
    private static final String BATCHES_TOPIC  = "/topic/batches";

    private final SimpMessagingTemplate messagingTemplate;

    public void publishStudentCreated(Long studentId, String email) {
        broadcast(STUDENTS_TOPIC, buildEvent("STUDENT_CREATED", studentId, Map.of("email", email)));
    }

    public void publishStudentUpdated(Long studentId, String email) {
        broadcast(STUDENTS_TOPIC, buildEvent("STUDENT_UPDATED", studentId, Map.of("email", email)));
    }

    public void publishStudentDeleted(Long studentId) {
        broadcast(STUDENTS_TOPIC, buildEvent("STUDENT_DELETED", studentId, Map.of()));
    }

    public void publishBatchCreated(Long batchId, String name) {
        broadcast(BATCHES_TOPIC, buildEvent("BATCH_CREATED", batchId, Map.of("name", name)));
    }

    public void publishBatchUpdated(Long batchId, String name) {
        broadcast(BATCHES_TOPIC, buildEvent("BATCH_UPDATED", batchId, Map.of("name", name)));
    }

    public void publishBatchDeleted(Long batchId) {
        broadcast(BATCHES_TOPIC, buildEvent("BATCH_DELETED", batchId, Map.of()));
    }

    // -------------------------------------------------------------------------

    private void broadcast(String destination, Map<String, Object> payload) {
        messagingTemplate.convertAndSend(destination, payload);
        log.debug("Event broadcast to {}: type={}", destination, payload.get("type"));
    }

    private Map<String, Object> buildEvent(String type, Long id, Map<String, Object> extra) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.putAll(extra);

        Map<String, Object> event = new HashMap<>();
        event.put("type", type);
        event.put("timestamp", Instant.now().toString());
        event.put("payload", payload);
        return event;
    }
}
