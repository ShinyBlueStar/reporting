package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.application.mapper.ReportExecutionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.ports.input.ReportExecutionService;
import com.sample.system.reporting.service.application.ports.output.IReportExecutionRepository;
import com.sample.system.reporting.service.application.ports.output.IReportFileRepository;
import com.sample.system.reporting.service.application.ports.output.IReportParameterRepository;
import com.sample.system.reporting.service.application.ports.output.IValidationRuleDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.ReportBatchLauncherPort;
import com.sample.system.reporting.service.application.ports.output.ReportFileStoragePort;
import com.sample.system.reporting.service.application.ports.output.ReportQueryExecutorPort;
import com.sample.system.reporting.service.application.validation.engine.ValidationEngine;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.application.response.reportexecution.ExecuteReportResponse;
import com.sample.system.reporting.service.application.response.reportexecution.ReportFileDownloadResponse;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchLaunchRequest;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.application.reportbatch.service.ReportBatchFormatResolver;
import com.sample.system.reporting.service.application.reportexecution.ReportExecutionTracker;
import com.sample.system.reporting.service.application.reportexecution.ReportExecutionUrlFactory;
import com.sample.system.reporting.service.application.reportexecution.ReportParameterBindingService;
import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import com.sample.system.reporting.service.domain.model.enums.ReportExecutionStatus;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExecutionServiceImpl implements ReportExecutionService {

    private static final int DEFAULT_MAX_EXPORT_ROWS = 20_000;

    private final ReportDefinitionService reportDefinitionService;
    private final IReportParameterRepository reportParameterRepository;
    private final IReportExecutionRepository reportExecutionRepository;
    private final ReportQueryExecutorPort reportQueryExecutorPort;
    private final ReportBatchLauncherPort reportBatchLauncherPort;
    private final ReportParameterBindingService reportParameterBindingService;
    private final ReportSqlGuard reportSqlGuard;
    private final ReportBatchFormatResolver reportBatchFormatResolver;
    private final ReportExecutionMapper reportExecutionMapper;
    private final IReportFileRepository reportFileRepository;
    private final ReportFileStoragePort reportFileStoragePort;
    private final ReportExecutionUrlFactory reportExecutionUrlFactory;
    private final ReportExecutionTracker executionTracker;
    private final IValidationRuleDefinitionRepository validationRuleDefinitionRepository;
    private final ValidationEngine validationEngine;

    @Value("${reporting.batch.chunk-size:1000}")
    private int batchChunkSize;

    @Override
    @Transactional
    public ExecuteReportResponse executeReport(ExecuteReportCommand command) {
        long started = System.currentTimeMillis();
        log.info("Starting report execution for definition id={}, requestedBy={}, parameterCount={}",
                command.getReportDefinitionId(),
                command.getRequestedBy(),
                command.getParameters() != null ? command.getParameters().size() : 0);

        ReportDefinition definition = reportDefinitionService.findReportDefinitionById(command.getReportDefinitionId());
        ensureActive(definition);
        log.info("Loaded active report definition id={}, code={}, name={}",
                definition.getId().getValue(),
                definition.getReportCode(),
                definition.getReportName());

        List<ReportParameter> parameterDefinitions =
                reportParameterRepository.findByReportDefinitionId(definition.getId().getValue());
        log.info("Loaded {} parameter definitions for definition id={}",
                parameterDefinitions.size(),
                definition.getId().getValue());
        Map<String, Object> boundParameters = reportParameterBindingService.resolveAndValidate(
                parameterDefinitions, command.getParameters());

        List<ValidationRuleDefinition> validationRules =
                validationRuleDefinitionRepository.findByReportDefinitionId(definition.getId().getValue());
        log.info("Loaded {} validation rules for definition id={}",
                validationRules.size(),
                definition.getId().getValue());
        validationEngine.validate(
                new ValidationContext(definition, boundParameters, command.getRequestedBy(),
                        Locale.getDefault()), validationRules);
        log.debug("Resolved {} bound parameters for definition id={}",
                boundParameters.size(),
                definition.getId().getValue());

        ReportExecution execution = startExecution(definition, command, boundParameters);
        log.info("Report execution started with id={} for definition id={}, code={}",
                execution.getId().getValue(),
                definition.getId().getValue(),
                definition.getReportCode());
        executionTracker.recordCompleted(execution, "VALIDATE", started, "Parameters validated");

        try {
            BatchExecutionResult batchResult = executionTracker.executeStep(
                    execution,
                    "EXECUTE_SQL",
                    "Executing SQL via batch",
                    "SQL executed",
                    () -> runBatch(definition, execution, command, boundParameters));

            finalizeSuccess(execution, started, batchResult.totalRecords());
            executionTracker.recordCompleted(execution, "FINALIZE", started, "Execution completed");

            String downloadUrl = reportExecutionUrlFactory.downloadUrl(execution.getId().getValue());
            log.info(
                    "Report execution completed for execution id={}, definition id={}, code={}, totalRecords={}, generatedFileId={}, format={}, downloadUrl={}, durationMs={}",
                    execution.getId().getValue(),
                    definition.getId().getValue(),
                    definition.getReportCode(),
                    batchResult.totalRecords(),
                    execution.getGeneratedFileId(),
                    batchResult.reportFormat(),
                    downloadUrl,
                    System.currentTimeMillis() - started);

            return reportExecutionMapper.toExecuteResponse(
                    definition, execution, batchResult.reportFormat(), downloadUrl);
        } catch (RuntimeException ex) {
            log.error(
                    "Report execution failed for execution id={}, definition id={}, code={}, requestedBy={}, durationMs={}",
                    execution.getId().getValue(),
                    definition.getId().getValue(),
                    definition.getReportCode(),
                    command.getRequestedBy(),
                    System.currentTimeMillis() - started,
                    ex);
            finalizeFailure(execution, started, ex.getMessage());
            executionTracker.recordFailed(execution, "FAILED", started, ex.getMessage());
            return reportExecutionMapper.toFailedExecuteResponse(definition, execution);
        }
    }

    @Override
    public ReportExecution findReportExecutionById(Long reportExecutionId) {
        ReportExecution execution = reportExecutionRepository.findById(reportExecutionId);
        if (execution == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_NOT_FOUND.getCode(),
                    ErrorCode.REPORT_EXECUTION_NOT_FOUND.name());
        }
        return execution;
    }

    @Override
    public ReportFileDownloadResponse downloadReportFile(Long reportExecutionId) {
        ReportExecution execution = findReportExecutionById(reportExecutionId);
        if (!ReportExecutionStatus.COMPLETED.name().equals(execution.getExecutionStatus())) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Report execution is not completed");
        }
        if (execution.getGeneratedFileId() == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Report file is not available for download");
        }

        ReportFile reportFile = reportFileRepository.findById(execution.getGeneratedFileId());
        if (reportFile == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Report file metadata not found");
        }

        Resource content = reportFileStoragePort.readContent(reportFile);
        String contentType = reportFile.getContentType() != null && !reportFile.getContentType().isBlank()
                ? reportFile.getContentType() : "application/octet-stream";
        String fileName = reportFile.getFileName() != null && !reportFile.getFileName().isBlank()
                ? reportFile.getFileName() : "report";
        return new ReportFileDownloadResponse(content, fileName, contentType);
    }

    private BatchExecutionResult runBatch(ReportDefinition definition,
                                          ReportExecution execution,
                                          ExecuteReportCommand command,
                                          Map<String, Object> boundParameters) {
        reportSqlGuard.ensureSelectableQuery(definition.getSqlQuery());
        ReportFormat reportFormat = reportBatchFormatResolver.resolve(command, definition);
        log.debug("Launching report batch for execution id={}, format={}, chunkSize={}",
                execution.getId().getValue(), reportFormat, batchChunkSize);

        ConcurrentMap<String, Object> sharedState = reportBatchLauncherPort.launch(
                new ReportBatchLaunchRequest(
                        definition,
                        execution,
                        reportFormat,
                        boundParameters,
                        batchChunkSize,
                        true));

        applyGeneratedFileFromBatchState(execution, sharedState);
        long totalRecords = totalRecords(definition, boundParameters, sharedState);
        return new BatchExecutionResult(reportFormat, totalRecords);
    }

    private void applyGeneratedFileFromBatchState(ReportExecution execution, ConcurrentMap<String, Object> sharedState) {
        Object batchExecution = sharedState.get(ReportBatchSharedStateKeys.REPORT_EXECUTION);
        if (batchExecution instanceof ReportExecution reportExecution && reportExecution.getGeneratedFileId() != null) {
            execution.setGeneratedFileId(reportExecution.getGeneratedFileId());
            return;
        }
        throw new ReportingDomainException(
                ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                ErrorCode.REPORT_EXECUTION_FAILED.name());
    }

    private long totalRecords(ReportDefinition definition,
                              Map<String, Object> boundParameters,
                              ConcurrentMap<String, Object> sharedState) {
        Object processed = sharedState.get(ReportBatchSharedStateKeys.PROCESSED_ROW_COUNT);
        long processedCount = processed instanceof Number number ? number.longValue() : 0L;
        int rowLimit = rowLimit(definition);
        if (processedCount > rowLimit) {
            return countTotalRecords(definition, boundParameters);
        }
        return processedCount;
    }

    private long countTotalRecords(ReportDefinition definition, Map<String, Object> boundParameters) {
        String countSql = "SELECT COUNT(*) AS cnt FROM (" + definition.getSqlQuery() + ") report_count_query";
        AtomicReference<Object> countValue = new AtomicReference<>();
        reportQueryExecutorPort.executeQuery(
                countSql,
                boundParameters,
                definition.getTimeoutSeconds(),
                1,
                row -> row.values().stream().findFirst().ifPresent(countValue::set));
        Object cnt = countValue.get();
        if (cnt == null) {
            return 0L;
        }
        if (cnt instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(cnt.toString());
    }

    private int rowLimit(ReportDefinition definition) {
        if (definition.getMaxExportRows() == null || definition.getMaxExportRows() <= 0) {
            return DEFAULT_MAX_EXPORT_ROWS;
        }
        return Math.toIntExact(Math.min(definition.getMaxExportRows(), Integer.MAX_VALUE));
    }

    private void ensureActive(ReportDefinition definition) {
        if (!Boolean.TRUE.equals(definition.getActive())) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_DEFINITION_INACTIVE.getCode(),
                    ErrorCode.REPORT_DEFINITION_INACTIVE.name());
        }
    }

    private ReportExecution startExecution(ReportDefinition definition,
                                           ExecuteReportCommand command,
                                           Map<String, Object> boundParameters) {
        ReportExecution execution = new ReportExecution();
        execution.setReportDefinitionId(definition.getId().getValue());
        execution.setExecutionStatus(ReportExecutionStatus.RUNNING.name());
        execution.setExecutionStartTime(Instant.now());
        execution.setRequestedBy(command.getRequestedBy());
        execution.setRequestParameters(reportParameterBindingService.toJson(boundParameters));
        execution.setExecutionSource(
                command.getRequestedBy() != null && command.getRequestedBy().startsWith("SCHEDULER:")
                        ? "SCHEDULED"
                        : "MANUAL");
        return reportExecutionRepository.save(execution);
    }

    private void finalizeSuccess(ReportExecution execution, long started, long totalRecords) {
        execution.setExecutionStatus(ReportExecutionStatus.COMPLETED.name());
        execution.setExecutionEndTime(Instant.now());
        execution.setExecutionDurationMs(System.currentTimeMillis() - started);
        execution.setTotalRecordCount(totalRecords);
        execution.setErrorMessage(null);
        reportExecutionRepository.save(execution);
    }

    private void finalizeFailure(ReportExecution execution, long started, String errorMessage) {
        execution.setExecutionStatus(ReportExecutionStatus.FAILED.name());
        execution.setExecutionEndTime(Instant.now());
        execution.setExecutionDurationMs(System.currentTimeMillis() - started);
        execution.setErrorMessage(truncate(errorMessage, 2000));
        reportExecutionRepository.save(execution);
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    private record BatchExecutionResult(ReportFormat reportFormat, long totalRecords) {
    }
}
