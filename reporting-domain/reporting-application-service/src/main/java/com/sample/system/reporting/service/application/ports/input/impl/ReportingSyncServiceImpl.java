package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.ReportingSyncService;
import com.sample.system.reporting.service.application.event.dispacher.ProjectionEventDispatcher;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;

import com.sample.system.reporting.service.application.ports.output.IReportEventRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportingSyncServiceImpl implements ReportingSyncService {

    private final ProjectionEventDispatcher projectionEventDispatcher;
    private final IReportEventRepository reportEventPort;

    public ReportingSyncServiceImpl(ProjectionEventDispatcher projectionEventDispatcher, IReportEventRepository reportEventPort) {
        this.projectionEventDispatcher = projectionEventDispatcher;
        this.reportEventPort = reportEventPort;
    }

    @Override
    public void sync(EventEnvelope eventEnvelope) {
        if (reportEventPort.isProcessed(eventEnvelope.eventId())) {
            return;
        }

        try {
            reportEventPort.markReceived(eventEnvelope);
            projectionEventDispatcher.dispatch(eventEnvelope);
            reportEventPort.markProcessed(eventEnvelope);
        } catch (RuntimeException ex) {
            reportEventPort.markFailed(eventEnvelope, ex.getMessage());
            throw ex;
        }
    }
}
