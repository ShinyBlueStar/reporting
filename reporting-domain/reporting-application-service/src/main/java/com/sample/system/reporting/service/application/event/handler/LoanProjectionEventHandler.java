package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.LoanReportProjectionService;
import org.springframework.stereotype.Component;

@Component
public class LoanProjectionEventHandler extends AbstractProjectionEventHandler {

    private final LoanReportProjectionService loanReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public LoanProjectionEventHandler(LoanReportProjectionService loanReportProjectionService,
                                      ReportingProjectionMapper reportingProjectionMapper) {
        super("LOAN");
        this.loanReportProjectionService = loanReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        loanReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToLoanReport(eventEnvelope));
    }
}
