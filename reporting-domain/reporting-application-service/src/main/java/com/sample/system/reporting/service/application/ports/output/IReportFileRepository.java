package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ReportFile;

public interface IReportFileRepository {

    ReportFile findById(Long id);
}
