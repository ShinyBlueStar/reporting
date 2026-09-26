package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.TransactionReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.TransactionReport;
import org.springframework.stereotype.Service;

@Service
public class TransactionReportProjectionServiceImpl implements TransactionReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public TransactionReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(TransactionReport transactionReport) {
        reportProjectionPort.upsertTransaction(transactionReport);
    }
}
