package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.TransactionReportProjectionService;
import org.springframework.stereotype.Component;

@Component
public class TransactionProjectionEventHandler extends AbstractProjectionEventHandler {

    private final TransactionReportProjectionService transactionReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public TransactionProjectionEventHandler(TransactionReportProjectionService transactionReportProjectionService,
                                             ReportingProjectionMapper reportingProjectionMapper) {
        super("TRANSACTION");
        this.transactionReportProjectionService = transactionReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        transactionReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToTransactionReport(eventEnvelope));
    }
}
