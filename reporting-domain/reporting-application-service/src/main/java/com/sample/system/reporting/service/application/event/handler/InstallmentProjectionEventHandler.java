package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.InstallmentReportProjectionService;
import org.springframework.stereotype.Component;

@Component
public class InstallmentProjectionEventHandler extends AbstractProjectionEventHandler {

    private final InstallmentReportProjectionService installmentReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public InstallmentProjectionEventHandler(InstallmentReportProjectionService installmentReportProjectionService,
                                             ReportingProjectionMapper reportingProjectionMapper) {
        super("INSTALLMENT");
        this.installmentReportProjectionService = installmentReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        installmentReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToInstallmentReport(eventEnvelope));
    }
}
