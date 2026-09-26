package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.PartyReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.PartyReport;

import org.springframework.stereotype.Service;

@Service
public class PartyReportProjectionServiceImpl implements PartyReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public PartyReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(PartyReport partyReport) {
        reportProjectionPort.upsertParty(partyReport);
    }
}
