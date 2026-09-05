package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ReportExecution;

public interface IReportExecutionRepository {

    ReportExecution save(ReportExecution reportExecution);

    ReportExecution findById(Long id);
}
