package com.sample.system.reporting.service.domain.ports.input.service.impl;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.event.dispacher.ProjectionEventDispatcher;
import com.sample.system.reporting.service.application.ports.input.impl.ReportingSyncServiceImpl;
import com.sample.system.reporting.service.application.ports.output.IReportEventRepository;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReportingSyncServiceImplTest {

    private final ProjectionEventDispatcher projectionEventDispatcher = mock(ProjectionEventDispatcher.class);
    private final IReportEventRepository reportEventPort = mock(IReportEventRepository.class);
    private final ReportingSyncServiceImpl service =
            new ReportingSyncServiceImpl(projectionEventDispatcher, reportEventPort);

    @Test
    void skipsAlreadyProcessedEvent() {
        EventEnvelope envelope = envelope();
        when(reportEventPort.isProcessed(envelope.eventId())).thenReturn(true);

        service.sync(envelope);

        verify(projectionEventDispatcher, never()).dispatch(envelope);
        verify(reportEventPort, never()).markReceived(envelope);
        verify(reportEventPort, never()).markProcessed(envelope);
    }

    @Test
    void dispatchesAndMarksProcessedEvent() {
        EventEnvelope envelope = envelope();

        service.sync(envelope);

        verify(reportEventPort).isProcessed(envelope.eventId());
        verify(reportEventPort).markReceived(envelope);
        verify(projectionEventDispatcher).dispatch(envelope);
        verify(reportEventPort).markProcessed(envelope);
        verify(reportEventPort, never()).markFailed(any(), anyString());
    }

    @Test
    void marksFailedAndRethrowsWhenDispatchFails() {
        EventEnvelope envelope = envelope();
        IllegalStateException failure = new IllegalStateException("projection failed");
        doThrow(failure).when(projectionEventDispatcher).dispatch(envelope);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> service.sync(envelope));

        assertEquals(failure, thrown);
        verify(reportEventPort).markReceived(envelope);
        verify(projectionEventDispatcher).dispatch(envelope);
        verify(reportEventPort).markFailed(envelope, "projection failed");
        verify(reportEventPort, never()).markProcessed(envelope);
    }

    @Test
    void marksFailedAndRethrowsWhenMarkReceivedFails() {
        EventEnvelope envelope = envelope();
        IllegalStateException failure = new IllegalStateException("cannot persist received event");
        doThrow(failure).when(reportEventPort).markReceived(envelope);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> service.sync(envelope));

        assertEquals(failure, thrown);
        verify(reportEventPort).markFailed(envelope, "cannot persist received event");
        verify(projectionEventDispatcher, never()).dispatch(envelope);
        verify(reportEventPort, never()).markProcessed(envelope);
    }

    private EventEnvelope envelope() {
        return new EventEnvelope(
                "event-1",
                "UPSERT",
                "PARTY",
                "party-1",
                1L,
                Instant.now(),
                "party-service",
                "correlation-1",
                Map.of()
        );
    }
}
