package com.academy.kafka;

import com.academy.config.KafkaConfig;
import com.academy.dto.EventDTO;
import com.academy.entity.MentorSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer for mentor session events
 * Report: Kafka Integration - Producer for mentor.session.created events
 * Report: Feature Development Process - Event published after session creation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MentorSessionEventProducer {
    
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    
    public void publishSessionCreatedEvent(MentorSession session) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sessionId", session.getId());
        payload.put("studentId", session.getStudent().getId());
        payload.put("mentorId", session.getMentor().getId());
        payload.put("time", session.getTime().toString());
        payload.put("durationMinutes", session.getDurationMinutes());
        
        EventDTO event = new EventDTO();
        event.setEventType("mentor.session.created");
        event.setTimestamp(Instant.now());
        event.setPayload(payload);
        
        try {
            kafkaTemplate.send(KafkaConfig.MENTOR_SESSION_CREATED_TOPIC, event);
            log.info("Published mentor.session.created event for session: {}", session.getId());
        } catch (Exception e) {
            log.error("Error publishing mentor.session.created event", e);
        }
    }
}

