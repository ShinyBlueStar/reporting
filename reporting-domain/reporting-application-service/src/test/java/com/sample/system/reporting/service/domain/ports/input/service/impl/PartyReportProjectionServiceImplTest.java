package com.sample.system.reporting.service.domain.ports.input.service.impl;

import com.sample.system.reporting.service.application.ports.input.impl.PartyReportProjectionServiceImpl;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.PartyReport;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PartyReportProjectionServiceImplTest {

    private final IReportProjectionRepository reportProjectionPort = mock(IReportProjectionRepository.class);
    private final PartyReportProjectionServiceImpl service =
            new PartyReportProjectionServiceImpl(reportProjectionPort);

    @Test
    void syncDelegatesToProjectionPort() {
        PartyReport report = new PartyReport();

        service.sync(report);

        verify(reportProjectionPort).upsertParty(report);
    }
}
