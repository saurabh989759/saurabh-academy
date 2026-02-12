package com.academy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Report: Kafka Integration - Event DTO for Kafka messages
 * Generic event DTO for Kafka event publishing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String eventType;
    private Instant timestamp;
    private Map<String, Object> payload;
}

