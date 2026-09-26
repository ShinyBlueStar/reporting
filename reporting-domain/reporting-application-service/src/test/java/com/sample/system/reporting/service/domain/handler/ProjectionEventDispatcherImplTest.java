package com.sample.system.reporting.service.domain.handler;

import com.sample.system.reporting.service.application.event.handler.ProjectionEventHandler;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.event.dispacher.impl.ProjectionEventDispatcherImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProjectionEventDispatcherImplTest {

    @Test
    void dispatchesToSupportedHandler() {
        EventEnvelope envelope = envelope("PARTY");
        ProjectionEventHandler supportedHandler = mock(ProjectionEventHandler.class);
        ProjectionEventHandler ignoredHandler = mock(ProjectionEventHandler.class);
        when(supportedHandler.supports(envelope)).thenReturn(true);

        new ProjectionEventDispatcherImpl(List.of(ignoredHandler, supportedHandler)).dispatch(envelope);

        verify(supportedHandler).handle(envelope);
        verify(ignoredHandler, never()).handle(envelope);
    }

    @Test
    void failsWhenNoHandlerSupportsEvent() {
        EventEnvelope envelope = envelope("UNKNOWN");

        assertThrows(IllegalArgumentException.class,
                () -> new ProjectionEventDispatcherImpl(List.of()).dispatch(envelope));
    }

    private EventEnvelope envelope(String aggregateType) {
        return new EventEnvelope(
                "event-1",
                "UPSERT",
                aggregateType,
                "aggregate-1",
                1L,
                Instant.now(),
                "test",
                "correlation-1",
                Map.of()
        );
    }
}
