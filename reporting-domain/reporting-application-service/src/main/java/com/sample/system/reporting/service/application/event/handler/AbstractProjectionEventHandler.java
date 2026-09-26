package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;

public abstract class AbstractProjectionEventHandler implements ProjectionEventHandler {

    private final String[] aggregateTypes;

    protected AbstractProjectionEventHandler(String... aggregateTypes) {
        this.aggregateTypes = aggregateTypes;
    }

    @Override
    public boolean supports(EventEnvelope eventEnvelope) {
        for (String aggregateType : aggregateTypes) {
            if (aggregateType.equalsIgnoreCase(eventEnvelope.aggregateType())) {
                return true;
            }
        }
        return false;
    }
}
