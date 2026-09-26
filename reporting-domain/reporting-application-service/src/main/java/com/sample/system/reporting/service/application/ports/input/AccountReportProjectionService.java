package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.AccountReport;

public interface AccountReportProjectionService {

    void sync(AccountReport accountReport);
}
