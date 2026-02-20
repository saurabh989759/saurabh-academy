package com.academy.kafka.producer;

import com.academy.dto.EventDTO;
import com.academy.entity.Batch;
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
public class BatchEventProducer {

    private final KafkaTemplate<String, EventDTO> kafkaTemplate;

    @Value("${kafka.topics.batch-created:batch.created}")
    private String topic;

    public void publishBatchCreatedEvent(Batch batch) {
        EventDTO event = buildEvent("batch.created", Map.of(
            "batchId", batch.getId(),
            "name", batch.getName(),
            "batchTypeId", batch.getBatchType().getId(),
            "startMonth", batch.getStartMonth().toString()
        ));

        dispatch(topic, event, batch.getId());
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
