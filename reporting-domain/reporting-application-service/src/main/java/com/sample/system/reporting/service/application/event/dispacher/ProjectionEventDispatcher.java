package com.sample.system.reporting.service.application.event.dispacher;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;

public interface ProjectionEventDispatcher {

    void dispatch(EventEnvelope eventEnvelope);
}
