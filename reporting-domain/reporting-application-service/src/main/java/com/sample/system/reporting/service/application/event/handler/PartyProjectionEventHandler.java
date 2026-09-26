package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.PartyReportProjectionService;
import org.springframework.stereotype.Component;

@Component
public class PartyProjectionEventHandler extends AbstractProjectionEventHandler {

    private final PartyReportProjectionService partyReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public PartyProjectionEventHandler(PartyReportProjectionService partyReportProjectionService,
                                       ReportingProjectionMapper reportingProjectionMapper) {
        super("PARTY", "CREDIT_FILE", "CREDITFILE", "CASE");
        this.partyReportProjectionService = partyReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        partyReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToPartyReport(eventEnvelope));
    }
}
