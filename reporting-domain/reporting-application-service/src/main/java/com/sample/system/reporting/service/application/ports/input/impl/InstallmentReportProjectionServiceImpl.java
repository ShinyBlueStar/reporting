package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.InstallmentReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.InstallmentReport;
import org.springframework.stereotype.Service;

@Service
public class InstallmentReportProjectionServiceImpl implements InstallmentReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public InstallmentReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(InstallmentReport installmentReport) {
        reportProjectionPort.upsertInstallment(installmentReport);
    }
}
