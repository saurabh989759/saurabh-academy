package com.academy.dto;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String eventType;
    private Instant timestamp;
    private Map<String, Object> payload;
}
