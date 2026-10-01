package com.sample.system.reporting.service.messaging.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sample.system.reporting.service.application.ports.input.ReportingSyncService;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportingEventProcessorTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ReportingSyncService reportingSyncService = mock(ReportingSyncService.class);
    private final ReportingEventProcessor processor = new ReportingEventProcessor(objectMapper, reportingSyncService);

    @Test
    void parsesValidEventAndDelegatesToSyncService() throws Exception {
        EventEnvelope envelope = envelope();

        processor.process(objectMapper.writeValueAsString(envelope));

        verify(reportingSyncService).sync(envelope);
    }

    @Test
    void throwsIllegalArgumentExceptionAndDoesNotSyncInvalidJson() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processor.process("not-json"));

        assertEquals("Invalid reporting event envelope", exception.getMessage());
        verify(reportingSyncService, never()).sync(any());
    }

    @Test
    void propagatesSyncFailuresForConsumerNackHandling() throws Exception {
        EventEnvelope envelope = envelope();
        doThrow(new IllegalStateException("projection failed")).when(reportingSyncService).sync(envelope);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> processor.process(objectMapper.writeValueAsString(envelope)));

        assertEquals("projection failed", exception.getMessage());
    }

    private EventEnvelope envelope() {
        return new EventEnvelope(
                "event-1",
                "UPSERT",
                "PARTY",
                "party-1",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "party-service",
                "correlation-1",
                Map.of("nationalCode", "1234567890")
        );
    }
}
