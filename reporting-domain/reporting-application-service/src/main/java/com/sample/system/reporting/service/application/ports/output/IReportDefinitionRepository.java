package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import org.springframework.data.domain.Page;

public interface IReportDefinitionRepository {

    ReportDefinition create(ReportDefinition reportDefinition);

    ReportDefinition update(ReportDefinition reportDefinition);

    ReportDefinition findById(Long id);

    ReportDefinition findByReportCode(String reportCode);

    ReportDefinition findByReportName(String reportName);

    Page<ReportDefinition> search(ReportDefinitionSearchCriteria criteria);

    void deleteById(Long id);

    ReportDefinition updateActive(Long id, Boolean active);
}
