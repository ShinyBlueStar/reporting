package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportParameterRepository;
import com.sample.system.reporting.service.dataaccess.mapper.ReportParameterDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ReportParameterRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportParameterPersistenceAdapter implements IReportParameterRepository {

    private final ReportParameterRepository reportParameterRepository;
    private final ReportParameterDataAccessMapper reportParameterDataAccessMapper;

    @Override
    public ReportParameter save(ReportParameter reportParameter) {
        return reportParameterDataAccessMapper.reportParameterEntityToReportParameter(
                reportParameterRepository.save(
                        reportParameterDataAccessMapper.reportParameterToReportParameterEntity(reportParameter)));
    }

    @Override
    public ReportParameter findById(Long id) {
        return reportParameterRepository.findById(id)
                .map(reportParameterDataAccessMapper::reportParameterEntityToReportParameter)
                .orElse(null);
    }

    @Override
    public List<ReportParameter> findByReportDefinitionId(Long reportDefinitionId) {
        return reportParameterRepository.findByReportDefinitionIdOrderByDisplayOrderAsc(reportDefinitionId)
                .stream()
                .map(reportParameterDataAccessMapper::reportParameterEntityToReportParameter)
                .toList();
    }

    @Override
    public ReportParameter findByReportDefinitionIdAndParameterName(Long reportDefinitionId, String parameterName) {
        return reportParameterRepository
                .findByReportDefinitionIdAndParameterName(reportDefinitionId, parameterName)
                .map(reportParameterDataAccessMapper::reportParameterEntityToReportParameter)
                .orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        reportParameterRepository.deleteById(id);
    }
}
