package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.TransactionReport;

public interface TransactionReportProjectionService {

    void sync(TransactionReport transactionReport);
}
