package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.mapper.ReportDefinitionDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ReportDefinitionRepository;
import com.sample.system.reporting.service.dataaccess.specification.ReportDefinitionSpecification;
import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportDefinitionPersistenceAdapter implements IReportDefinitionRepository {

    private final ReportDefinitionRepository reportDefinitionRepository;
    private final ReportDefinitionDataAccessMapper reportDefinitionDataAccessMapper;

    @Override
    public ReportDefinition create(ReportDefinition reportDefinition) {
        ReportDefinitionEntity entity = reportDefinitionDataAccessMapper
                .reportDefinitionToReportDefinitionEntity(reportDefinition);
        return reportDefinitionDataAccessMapper.reportDefinitionEntityToReportDefinition(
                reportDefinitionRepository.save(entity));
    }

    @Override
    public ReportDefinition update(ReportDefinition reportDefinition) {
        ReportDefinitionEntity entity = reportDefinitionDataAccessMapper
                .reportDefinitionToReportDefinitionEntity(reportDefinition);
        return reportDefinitionDataAccessMapper.reportDefinitionEntityToReportDefinition(
                reportDefinitionRepository.save(entity));
    }

    @Override
    public ReportDefinition findById(Long id) {
        return reportDefinitionRepository.findById(id)
                .map(reportDefinitionDataAccessMapper::reportDefinitionEntityToReportDefinition)
                .orElse(null);
    }

    @Override
    public ReportDefinition findByReportCode(String reportCode) {
        return reportDefinitionRepository.findByReportCode(reportCode)
                .map(reportDefinitionDataAccessMapper::reportDefinitionEntityToReportDefinition)
                .orElse(null);
    }

    @Override
    public ReportDefinition findByReportName(String reportName) {
        return reportDefinitionRepository.findByReportName(reportName)
                .map(reportDefinitionDataAccessMapper::reportDefinitionEntityToReportDefinition)
                .orElse(null);
    }

    @Override
    public Page<ReportDefinition> search(ReportDefinitionSearchCriteria criteria) {
        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportDefinitionEntity> entityPage =
                reportDefinitionRepository.findAll(ReportDefinitionSpecification.from(criteria), pageable);

        List<ReportDefinition> content = entityPage.getContent().stream()
                .map(reportDefinitionDataAccessMapper::reportDefinitionEntityToReportDefinition)
                .toList();
        return new PageImpl<>(content, pageable, entityPage.getTotalElements());
    }

    @Override
    public void deleteById(Long id) {
        reportDefinitionRepository.deleteById(id);
    }

    @Override
    public ReportDefinition updateActive(Long id, Boolean active) {
        return reportDefinitionRepository.findById(id)
                .map(entity -> {
                    entity.setActive(active);
                    return reportDefinitionDataAccessMapper.reportDefinitionEntityToReportDefinition(
                            reportDefinitionRepository.save(entity));
                })
                .orElse(null);
    }
}
