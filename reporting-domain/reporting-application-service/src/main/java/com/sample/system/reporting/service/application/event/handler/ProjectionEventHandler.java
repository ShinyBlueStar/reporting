package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;

public interface ProjectionEventHandler {

    boolean supports(EventEnvelope eventEnvelope);

    void handle(EventEnvelope eventEnvelope);
}
