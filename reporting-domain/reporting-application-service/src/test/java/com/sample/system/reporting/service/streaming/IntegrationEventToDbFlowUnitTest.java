package com.sample.system.reporting.service.streaming;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.event.handler.PartyProjectionEventHandler;
import com.sample.system.reporting.service.application.event.dispacher.ProjectionEventDispatcher;
import com.sample.system.reporting.service.application.event.dispacher.impl.ProjectionEventDispatcherImpl;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.PartyReportProjectionService;
import com.sample.system.reporting.service.application.ports.input.impl.PartyReportProjectionServiceImpl;
import com.sample.system.reporting.service.application.ports.input.impl.ReportingSyncServiceImpl;
import com.sample.system.reporting.service.application.ports.output.IReportEventRepository;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class IntegrationEventToDbFlowUnitTest {

    @Test
    void syncsPartyEventIntoProjectionPort() {
        IReportProjectionRepository projectionPort = mock(IReportProjectionRepository.class);
        IReportEventRepository reportEventPort = mock(IReportEventRepository.class);
        when(reportEventPort.isProcessed("event-1")).thenReturn(false);

        PartyReportProjectionService partyService = new PartyReportProjectionServiceImpl(projectionPort);
        ReportingProjectionMapper mapper = new ReportingProjectionMapper();
        PartyProjectionEventHandler handler = new PartyProjectionEventHandler(partyService, mapper);
        ProjectionEventDispatcher dispatcher = new ProjectionEventDispatcherImpl(List.of(handler));
        ReportingSyncServiceImpl syncService = new ReportingSyncServiceImpl(dispatcher, reportEventPort);

        EventEnvelope envelope = new EventEnvelope(
                "event-1",
                "UPSERT",
                "PARTY",
                "party-1",
                3L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "party-service",
                "corr-1",
                Map.of("nationalCode", "1234567890", "partyName", "Ali")
        );

        syncService.sync(envelope);

        verify(reportEventPort).markReceived(envelope);
        verify(projectionPort).upsertParty(argThat(r ->
                "party-1".equals(r.getAggregateId())
                        && Long.valueOf(3L).equals(r.getAggregateVersion())
                        && "1234567890".equals(r.getNationalCode())
                        && "Ali".equals(r.getPartyName())
        ));
        verify(reportEventPort).markProcessed(envelope);
    }
}
