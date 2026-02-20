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
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void onStudentRegistered(
            @Payload EventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack) {

        log.info("Received '{}' from topic={} partition={} offset={}", event.getEventType(), topic, partition, offset);
        processAndAcknowledge(event, ack);
    }

    @KafkaListener(
        topics = "${kafka.topics.mentor-session-created:mentor.session.created}",
        groupId = "${kafka.consumer.group-id:academy-kafka-consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void onMentorSessionCreated(
            @Payload EventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack) {

        log.info("Received '{}' from topic={} partition={} offset={}", event.getEventType(), topic, partition, offset);
        processAndAcknowledge(event, ack);
    }

    @KafkaListener(
        topics = "${kafka.topics.batch-created:batch.created}",
        groupId = "${kafka.consumer.group-id:academy-kafka-consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void onBatchCreated(
            @Payload EventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack) {

        log.info("Received '{}' from topic={} partition={} offset={}", event.getEventType(), topic, partition, offset);
        processAndAcknowledge(event, ack);
    }

    // -------------------------------------------------------------------------

    private void processAndAcknowledge(EventDTO event, Acknowledgment ack) {
        try {
            persist(event);
            log.info("Event '{}' processed and stored", event.getEventType());
            if (ack != null) ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process event '{}', scheduling retry", event.getEventType(), ex);
            throw ex;
        }
    }

    private void persist(EventDTO event) {
        try {
            AuditEvent record = new AuditEvent();
            record.setEventType(event.getEventType());
            record.setPayload(objectMapper.writeValueAsString(event));
            record.setCreatedAt(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());
            auditEventRepository.save(record);
            log.debug("Audit record stored for event '{}'", event.getEventType());
        } catch (JsonProcessingException ex) {
            log.error("JSON serialization failed for event '{}'", event.getEventType(), ex);
            throw new RuntimeException("Audit persistence failed for: " + event.getEventType(), ex);
        }
    }
}
