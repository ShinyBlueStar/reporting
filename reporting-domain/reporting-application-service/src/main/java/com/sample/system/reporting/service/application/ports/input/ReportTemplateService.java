package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import java.util.List;

public interface ReportTemplateService {
    ReportTemplate create(ReportTemplate template);
    ReportTemplate update(ReportTemplate template);
    ReportTemplate findById(Long id);
    List<ReportTemplate> findByReportDefinitionId(Long reportDefinitionId);
    void delete(Long id);
}
