package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.LoanReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.LoanReport;
import org.springframework.stereotype.Service;

@Service
public class LoanReportProjectionServiceImpl implements LoanReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public LoanReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(LoanReport loanReport) {
        reportProjectionPort.upsertLoan(loanReport);
    }
}
