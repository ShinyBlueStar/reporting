package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ReportDefinitionServiceImpl implements ReportDefinitionService {

    private final IReportDefinitionRepository reportDefinitionRepository;

    @Override
    public ReportDefinition createReportDefinition(ReportDefinition reportDefinition) {
        log.info("Creating report definition with code: {}",
                reportDefinition != null ? reportDefinition.getReportCode() : null);

        if (reportDefinition == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_REQUIRED.getCode(),
                    ErrorCode.REPORT_DEFINITION_REQUIRED.name());
        }
        if (reportDefinition.getReportCode() == null || reportDefinition.getReportCode().isBlank()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_CODE_REQUIRED.getCode(),
                    ErrorCode.REPORT_CODE_REQUIRED.name());
        }
        if (reportDefinition.getReportName() == null || reportDefinition.getReportName().isBlank()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_NAME_REQUIRED.getCode(),
                    ErrorCode.REPORT_NAME_REQUIRED.name());
        }
        if (reportDefinitionRepository.findByReportCode(reportDefinition.getReportCode()) != null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.getCode(),
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.name());
        }
        if (reportDefinitionRepository.findByReportName(reportDefinition.getReportName()) != null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.getCode(),
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.name());
        }
        return reportDefinitionRepository.create(reportDefinition);
    }

    @Override
    public ReportDefinition updateReportDefinition(ReportDefinition reportDefinition) {
        log.info("Updating report definition with id: {}",
                reportDefinition != null && reportDefinition.getId() != null
                        ? reportDefinition.getId().getValue() : null);

        if (reportDefinition == null || reportDefinition.getId() == null || reportDefinition.getId().getValue() == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_ID_REQUIRED.getCode(),
                    ErrorCode.REPORT_DEFINITION_ID_REQUIRED.name());
        }

        ReportDefinition existing = reportDefinitionRepository.findById(reportDefinition.getId().getValue());
        if (existing == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.getCode(),
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.name());
        }

        ReportDefinition duplicate = reportDefinitionRepository.findByReportCode(reportDefinition.getReportCode());
        if (duplicate != null && !duplicate.getId().getValue().equals(reportDefinition.getId().getValue())) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.getCode(),
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.name());
        }

        ReportDefinition duplicateName = reportDefinitionRepository.findByReportName(reportDefinition.getReportName());
        if (duplicateName != null && !duplicateName.getId().getValue().equals(reportDefinition.getId().getValue())) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.getCode(),
                    ErrorCode.REPORT_DEFINITION_DUPLICATE.name());
        }

        return reportDefinitionRepository.update(reportDefinition);
    }

    @Override
    public ReportDefinition findReportDefinitionById(Long reportDefinitionId) {
        log.info("Finding report definition with id: {}", reportDefinitionId);
        ReportDefinition definition = reportDefinitionRepository.findById(reportDefinitionId);
        if (definition == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.getCode(),
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.name());
        }
        return definition;
    }

    @Override
    public ReportDefinition findReportDefinitionByCode(String reportDefinitionCode) {
        log.info("Finding report definition with code: {}", reportDefinitionCode);
        if (reportDefinitionCode == null || reportDefinitionCode.isBlank()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_CODE_REQUIRED.getCode(), ErrorCode.REPORT_CODE_REQUIRED.name());
        }
        ReportDefinition definition = reportDefinitionRepository.findByReportCode(reportDefinitionCode);
        if (definition == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.getCode(), ErrorCode.REPORT_DEFINITION_NOT_FOUND.name());
        }
        return definition;
    }

    @Override
    public Page<ReportDefinition> searchReportDefinitions(ReportDefinitionSearchCriteria criteria) {
        log.info("Searching report definitions");
        return reportDefinitionRepository.search(criteria);
    }

    @Override
    public void deleteReportDefinition(Long reportDefinitionId) {
        log.info("Deleting report definition with id: {}", reportDefinitionId);
        if (reportDefinitionId == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_ID_REQUIRED.getCode(),
                    ErrorCode.REPORT_DEFINITION_ID_REQUIRED.name());
        }
        if (reportDefinitionRepository.findById(reportDefinitionId) == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.getCode(),
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.name());
        }
        reportDefinitionRepository.deleteById(reportDefinitionId);
    }

    @Override
    public ReportDefinition updateReportDefinitionStatus(Long reportDefinitionId, Boolean active) {
        log.info("Updating report definition status for id: {} to active={}", reportDefinitionId, active);
        ReportDefinition updated = reportDefinitionRepository.updateActive(reportDefinitionId, active);
        if (updated == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.getCode(),
                    ErrorCode.REPORT_DEFINITION_NOT_FOUND.name());
        }
        return updated;
    }
}
