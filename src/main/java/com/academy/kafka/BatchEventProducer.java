package com.academy.kafka;

import com.academy.config.KafkaConfig;
import com.academy.dto.EventDTO;
import com.academy.entity.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer for batch events
 * Report: Kafka Integration - Producer for batch.created events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchEventProducer {
    
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    
    public void publishBatchCreatedEvent(Batch batch) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("batchId", batch.getId());
        payload.put("name", batch.getName());
        payload.put("batchTypeId", batch.getBatchType().getId());
        payload.put("startMonth", batch.getStartMonth().toString());
        
        EventDTO event = new EventDTO();
        event.setEventType("batch.created");
        event.setTimestamp(Instant.now());
        event.setPayload(payload);
        
        try {
            kafkaTemplate.send(KafkaConfig.BATCH_CREATED_TOPIC, event);
            log.info("Published batch.created event for batch: {}", batch.getId());
        } catch (Exception e) {
            log.error("Error publishing batch.created event", e);
        }
    }
}

