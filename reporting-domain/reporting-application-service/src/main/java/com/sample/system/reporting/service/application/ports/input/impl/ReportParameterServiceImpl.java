package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.ReportParameterService;
import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.IReportParameterRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportParameterServiceImpl implements ReportParameterService {
    private final IReportParameterRepository repository;
    private final IReportDefinitionRepository reportDefinitionRepository;

    @Override
    @Transactional
    public ReportParameter create(ReportParameter parameter) {
        validate(parameter);
        ensureParentExists(parameter.getReportDefinitionId());
        if (repository.findByReportDefinitionIdAndParameterName(
                parameter.getReportDefinitionId(), parameter.getParameterName()) != null) {
            throw error(ErrorCode.REPORT_PARAMETER_DUPLICATE);
        }
        return repository.save(parameter);
    }

    @Override
    @Transactional
    public ReportParameter update(ReportParameter parameter) {
        validate(parameter);
        if (parameter.getId() == null || parameter.getId().getValue() == null) {
            throw error(ErrorCode.REPORT_PARAMETER_INVALID);
        }
        findById(parameter.getId().getValue());
        ensureParentExists(parameter.getReportDefinitionId());
        ReportParameter duplicate = repository.findByReportDefinitionIdAndParameterName(
                parameter.getReportDefinitionId(), parameter.getParameterName());
        if (duplicate != null && !duplicate.getId().getValue().equals(parameter.getId().getValue())) {
            throw error(ErrorCode.REPORT_PARAMETER_DUPLICATE);
        }
        return repository.save(parameter);
    }

    @Override
    public ReportParameter findById(Long id) {
        ReportParameter result = id == null ? null : repository.findById(id);
        if (result == null) throw error(ErrorCode.REPORT_PARAMETER_NOT_FOUND);
        return result;
    }

    @Override
    public List<ReportParameter> findByReportDefinitionId(Long reportDefinitionId) {
        ensureParentExists(reportDefinitionId);
        return repository.findByReportDefinitionId(reportDefinitionId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    private void validate(ReportParameter parameter) {
        if (parameter == null || parameter.getReportDefinitionId() == null
                || blank(parameter.getParameterName()) || blank(parameter.getParameterLabel())
                || blank(parameter.getParameterType())) {
            throw error(ErrorCode.REPORT_PARAMETER_INVALID);
        }
        if (parameter.getMinLength() != null && parameter.getMaxLength() != null
                && parameter.getMinLength() > parameter.getMaxLength()) {
            throw error(ErrorCode.REPORT_PARAMETER_INVALID);
        }
    }

    private void ensureParentExists(Long id) {
        if (id == null || reportDefinitionRepository.findById(id) == null) {
            throw error(ErrorCode.REPORT_DEFINITION_NOT_FOUND);
        }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
    private ReportingDomainException error(ErrorCode code) {
        return new ReportingDomainException(code.getCode(), code.name());
    }
}
