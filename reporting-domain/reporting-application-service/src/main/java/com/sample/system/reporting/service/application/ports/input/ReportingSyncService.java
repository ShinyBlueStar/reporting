package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;

public interface ReportingSyncService {

    void sync(EventEnvelope eventEnvelope);
}
