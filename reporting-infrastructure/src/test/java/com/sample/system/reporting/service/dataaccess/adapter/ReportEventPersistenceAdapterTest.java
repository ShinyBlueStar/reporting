package com.sample.system.reporting.service.dataaccess.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sample.system.reporting.service.dataaccess.entity.query.ReportEventEntity;
import com.sample.system.reporting.service.dataaccess.repository.ReportEventRepository;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.domain.model.enums.EventStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportEventPersistenceAdapterTest {

    private final ReportEventRepository repository = mock(ReportEventRepository.class);
    private final IReportEventPersistenceAdapter adapter =
            new IReportEventPersistenceAdapter(repository, new ObjectMapper().registerModule(new JavaTimeModule()));

    @Test
    void checksProcessedStatusByEventId() {
        when(repository.existsByEventIdAndStatus("event-1", EventStatus.PROCESSED)).thenReturn(true);

        assertTrue(adapter.isProcessed("event-1"));

        verify(repository).existsByEventIdAndStatus("event-1", EventStatus.PROCESSED);
    }

    @Test
    void markReceivedCreatesReceivedEventAndClearsPreviousProcessingState() {
        EventEnvelope envelope = envelope();
        when(repository.findByEventId(envelope.eventId())).thenReturn(Optional.empty());

        adapter.markReceived(envelope);

        ReportEventEntity saved = savedEntity();
        assertEquals("event-1", saved.getEventId());
        assertEquals("UPSERT", saved.getEventType());
        assertEquals("PARTY", saved.getAggregateType());
        assertEquals("party-1", saved.getAggregateId());
        assertEquals(1L, saved.getAggregateVersion());
        assertEquals("party-service", saved.getSourceSystem());
        assertEquals("correlation-1", saved.getCorrelationId());
        assertEquals(Instant.parse("2026-06-01T12:00:00Z"), saved.getEventTimestamp());
        assertEquals(EventStatus.RECEIVED, saved.getStatus());
        assertNull(saved.getProcessedAt());
        assertNull(saved.getErrorMessage());
        assertTrue(saved.getPayload().contains("\"nationalCode\":\"1234567890\""));
    }

    @Test
    void markProcessedUpdatesExistingEventAsProcessedAndClearsError() {
        EventEnvelope envelope = envelope();
        ReportEventEntity existing = new ReportEventEntity();
        existing.setEventId(envelope.eventId());
        existing.setErrorMessage("old error");
        when(repository.findByEventId(envelope.eventId())).thenReturn(Optional.of(existing));

        adapter.markProcessed(envelope);

        ReportEventEntity saved = savedEntity();
        assertEquals(existing, saved);
        assertEquals(EventStatus.PROCESSED, saved.getStatus());
        assertNotNull(saved.getProcessedAt());
        assertNull(saved.getErrorMessage());
        assertEquals("event-1", saved.getEventId());
    }

    @Test
    void markFailedUpdatesExistingEventAsFailedWithErrorMessage() {
        EventEnvelope envelope = envelope();
        ReportEventEntity existing = new ReportEventEntity();
        existing.setEventId(envelope.eventId());
        when(repository.findByEventId(envelope.eventId())).thenReturn(Optional.of(existing));

        adapter.markFailed(envelope, "projection failed");

        ReportEventEntity saved = savedEntity();
        assertEquals(EventStatus.FAILED, saved.getStatus());
        assertEquals("projection failed", saved.getErrorMessage());
        assertNotNull(saved.getProcessedAt());
        assertEquals("event-1", saved.getEventId());
    }

    private ReportEventEntity savedEntity() {
        ArgumentCaptor<ReportEventEntity> captor = ArgumentCaptor.forClass(ReportEventEntity.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
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
