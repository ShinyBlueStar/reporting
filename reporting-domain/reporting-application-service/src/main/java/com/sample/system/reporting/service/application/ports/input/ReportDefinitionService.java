package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ReportDefinitionService {

    ReportDefinition createReportDefinition(@Valid ReportDefinition reportDefinition);

    ReportDefinition updateReportDefinition(@Valid ReportDefinition reportDefinition);

    ReportDefinition findReportDefinitionById(Long reportDefinitionId);

    ReportDefinition findReportDefinitionByCode(String reportDefinitionCode);

    Page<ReportDefinition> searchReportDefinitions(ReportDefinitionSearchCriteria criteria);

    void deleteReportDefinition(Long reportDefinitionId);

    ReportDefinition updateReportDefinitionStatus(Long reportDefinitionId, Boolean active);
}
