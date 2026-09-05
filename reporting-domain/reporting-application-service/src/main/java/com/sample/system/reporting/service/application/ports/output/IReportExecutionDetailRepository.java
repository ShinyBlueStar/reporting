package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ReportExecutionDetail;

public interface IReportExecutionDetailRepository {

    ReportExecutionDetail save(ReportExecutionDetail detail);
}
