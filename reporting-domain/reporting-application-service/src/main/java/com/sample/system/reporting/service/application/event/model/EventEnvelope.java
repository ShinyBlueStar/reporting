package com.sample.system.reporting.service.application.event.model;

import java.time.Instant;
import java.util.Map;

public record EventEnvelope(
        String eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        Long aggregateVersion,
        Instant occurredAt,
        String source,
        String correlationId,
        Map<String, Object> payload
) {
}
