package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;

public interface IReportEventRepository {

    boolean isProcessed(String eventId);

    void markReceived(EventEnvelope eventEnvelope);

    void markProcessed(EventEnvelope eventEnvelope);

    void markFailed(EventEnvelope eventEnvelope, String errorMessage);
}
