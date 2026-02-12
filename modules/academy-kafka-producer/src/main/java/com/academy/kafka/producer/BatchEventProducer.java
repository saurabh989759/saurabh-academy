package com.academy.kafka.producer;

import com.academy.dto.EventDTO;
import com.academy.entity.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer for batch events
 * Module: academy-kafka-producer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchEventProducer {
    
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    
    @Value("${kafka.topics.batch-created:batch.created}")
    private String batchCreatedTopic;
    
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
            kafkaTemplate.send(batchCreatedTopic, event);
            log.info("Published batch.created event for batch: {}", batch.getId());
        } catch (Exception e) {
            log.error("Error publishing batch.created event", e);
            throw new RuntimeException("Failed to publish batch.created event", e);
        }
    }
}

