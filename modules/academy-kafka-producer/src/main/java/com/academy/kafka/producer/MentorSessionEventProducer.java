package com.academy.kafka.producer;

import com.academy.dto.EventDTO;
import com.academy.entity.MentorSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer for mentor session events
 * Module: academy-kafka-producer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MentorSessionEventProducer {
    
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    
    @Value("${kafka.topics.mentor-session-created:mentor.session.created}")
    private String mentorSessionCreatedTopic;
    
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
            kafkaTemplate.send(mentorSessionCreatedTopic, event);
            log.info("Published mentor.session.created event for session: {}", session.getId());
        } catch (Exception e) {
            log.error("Error publishing mentor.session.created event", e);
            throw new RuntimeException("Failed to publish mentor.session.created event", e);
        }
    }
}

