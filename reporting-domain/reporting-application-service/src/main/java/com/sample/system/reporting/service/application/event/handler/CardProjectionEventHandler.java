package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.ports.input.CardReportProjectionService;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import org.springframework.stereotype.Component;

@Component
public class CardProjectionEventHandler extends AbstractProjectionEventHandler {

    private final CardReportProjectionService cardReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public CardProjectionEventHandler(CardReportProjectionService cardReportProjectionService,
                                      ReportingProjectionMapper reportingProjectionMapper) {
        super("CARD");
        this.cardReportProjectionService = cardReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        cardReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToCardReport(eventEnvelope));
    }
}
