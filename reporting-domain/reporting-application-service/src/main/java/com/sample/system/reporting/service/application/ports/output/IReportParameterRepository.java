package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ReportParameter;

import java.util.List;

public interface IReportParameterRepository {

    ReportParameter save(ReportParameter reportParameter);

    ReportParameter findById(Long id);

    List<ReportParameter> findByReportDefinitionId(Long reportDefinitionId);

    ReportParameter findByReportDefinitionIdAndParameterName(Long reportDefinitionId, String parameterName);

    void deleteById(Long id);
}
