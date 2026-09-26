package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.InstallmentReport;

public interface InstallmentReportProjectionService {

    void sync(InstallmentReport installmentReport);
}
