package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;

import com.sample.system.reporting.service.application.ports.input.AccountReportProjectionService;
import org.springframework.stereotype.Component;

@Component
public class AccountProjectionEventHandler extends AbstractProjectionEventHandler {

    private final AccountReportProjectionService accountReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public AccountProjectionEventHandler(AccountReportProjectionService accountReportProjectionService,
                                         ReportingProjectionMapper reportingProjectionMapper) {
        super("ACCOUNT");
        this.accountReportProjectionService = accountReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        accountReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToAccountReport(eventEnvelope));
    }
}
