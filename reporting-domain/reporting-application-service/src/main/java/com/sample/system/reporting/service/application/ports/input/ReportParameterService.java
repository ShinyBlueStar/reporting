package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import java.util.List;

public interface ReportParameterService {
    ReportParameter create(ReportParameter parameter);
    ReportParameter update(ReportParameter parameter);
    ReportParameter findById(Long id);
    List<ReportParameter> findByReportDefinitionId(Long reportDefinitionId);
    void delete(Long id);
}
