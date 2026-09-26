package com.sample.system.reporting.service.domain.handler;

import com.sample.system.reporting.service.application.event.handler.PartyProjectionEventHandler;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.PartyReportProjectionService;
import com.sample.system.reporting.service.domain.model.entity.PartyReport;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PartyProjectionProjectionEventHandlerTest {

    private final PartyReportProjectionService projectionService = mock(PartyReportProjectionService.class);
    private final ReportingProjectionMapper mapper = mock(ReportingProjectionMapper.class);
    private final PartyProjectionEventHandler handler = new PartyProjectionEventHandler(projectionService, mapper);

    @Test
    void supportsPartyAggregateType() {
        EventEnvelope envelope = new EventEnvelope(
                "event-1", "UPSERT", "PARTY", "party-1", 1L,
                Instant.now(), "party-service", "corr-1", Map.of());
        PartyReport report = new PartyReport();
        when(mapper.eventEnvelopeToPartyReport(envelope)).thenReturn(report);

        assertTrue(handler.supports(envelope));
        handler.handle(envelope);

        verify(mapper).eventEnvelopeToPartyReport(envelope);
        verify(projectionService).sync(report);
    }
}
