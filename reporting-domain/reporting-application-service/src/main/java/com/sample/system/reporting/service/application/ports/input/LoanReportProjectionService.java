package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.LoanReport;

public interface LoanReportProjectionService {

    void sync(LoanReport loanReport);
}
