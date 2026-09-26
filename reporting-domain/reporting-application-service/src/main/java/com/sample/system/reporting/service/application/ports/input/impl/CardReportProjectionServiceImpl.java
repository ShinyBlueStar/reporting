package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.CardReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.CardReport;
import org.springframework.stereotype.Service;

@Service
public class CardReportProjectionServiceImpl implements CardReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public CardReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(CardReport cardReport) {
        reportProjectionPort.upsertCard(cardReport);
    }
}
