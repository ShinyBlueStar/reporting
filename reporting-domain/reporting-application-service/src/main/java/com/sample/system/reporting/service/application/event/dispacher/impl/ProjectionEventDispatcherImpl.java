package com.sample.system.reporting.service.application.event.dispacher.impl;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.event.dispacher.ProjectionEventDispatcher;
import com.sample.system.reporting.service.application.event.handler.ProjectionEventHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectionEventDispatcherImpl implements ProjectionEventDispatcher {

    private final List<ProjectionEventHandler> projectionEventHandlers;

    public ProjectionEventDispatcherImpl(List<ProjectionEventHandler> projectionEventHandlers) {
        this.projectionEventHandlers = projectionEventHandlers;
    }

    @Override
    public void dispatch(EventEnvelope eventEnvelope) {
        projectionEventHandlers.stream()
                .filter(handler -> handler.supports(eventEnvelope))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported event type: " + eventEnvelope.eventType()))
                .handle(eventEnvelope);
    }
}
