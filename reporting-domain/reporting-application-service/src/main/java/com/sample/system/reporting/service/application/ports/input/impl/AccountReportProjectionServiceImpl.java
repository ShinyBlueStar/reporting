package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.AccountReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.AccountReport;
import org.springframework.stereotype.Service;

@Service
public class AccountReportProjectionServiceImpl implements AccountReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public AccountReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(AccountReport accountReport) {
        reportProjectionPort.upsertAccount(accountReport);
    }
}
