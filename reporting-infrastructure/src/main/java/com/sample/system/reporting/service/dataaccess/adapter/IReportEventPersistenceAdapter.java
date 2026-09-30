package com.sample.system.reporting.service.dataaccess.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.ports.output.IReportEventRepository;
import com.sample.system.reporting.service.dataaccess.entity.query.ReportEventEntity;
import com.sample.system.reporting.service.dataaccess.repository.ReportEventRepository;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.domain.model.enums.EventStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class IReportEventPersistenceAdapter implements IReportEventRepository {

    private final ReportEventRepository reportEventRepository;
    private final ObjectMapper objectMapper;

    public IReportEventPersistenceAdapter(ReportEventRepository reportEventRepository, ObjectMapper objectMapper) {
        this.reportEventRepository = reportEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isProcessed(String eventId) {
        return reportEventRepository.existsByEventIdAndStatus(eventId, EventStatus.PROCESSED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markReceived(EventEnvelope eventEnvelope) {
        ReportEventEntity entity = reportEventRepository.findByEventId(eventEnvelope.eventId())
                .orElseGet(ReportEventEntity::new);
        fillFromEnvelope(entity, eventEnvelope);
        entity.setStatus(EventStatus.RECEIVED);
        entity.setProcessedAt(null);
        entity.setErrorMessage(null);
        reportEventRepository.save(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessed(EventEnvelope eventEnvelope) {
        ReportEventEntity entity = reportEventRepository.findByEventId(eventEnvelope.eventId())
                .orElseGet(() -> new ReportEventEntity());
        fillFromEnvelope(entity, eventEnvelope);
        entity.setStatus(EventStatus.PROCESSED);
        entity.setProcessedAt(Instant.now());
        entity.setErrorMessage(null);
        reportEventRepository.save(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(EventEnvelope eventEnvelope, String errorMessage) {
        ReportEventEntity entity = reportEventRepository.findByEventId(eventEnvelope.eventId())
                .orElseGet(() -> new ReportEventEntity());
        fillFromEnvelope(entity, eventEnvelope);
        entity.setStatus(EventStatus.FAILED);
        entity.setProcessedAt(Instant.now());
        entity.setErrorMessage(errorMessage);
        reportEventRepository.save(entity);
    }

    private void fillFromEnvelope(ReportEventEntity entity, EventEnvelope eventEnvelope) {
        if (entity.getEventId() == null) {
            entity.setEventId(eventEnvelope.eventId());
        }
        entity.setEventType(eventEnvelope.eventType());
        entity.setAggregateType(eventEnvelope.aggregateType());
        entity.setAggregateId(eventEnvelope.aggregateId());
        entity.setAggregateVersion(eventEnvelope.aggregateVersion());
        entity.setSourceSystem(eventEnvelope.source());
        entity.setCorrelationId(eventEnvelope.correlationId());
        entity.setEventTimestamp(eventEnvelope.occurredAt());
        entity.setPayload(toJson(eventEnvelope));
    }

    private String toJson(EventEnvelope eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope.payload());
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot serialize event payload", ex);
        }
    }
}

