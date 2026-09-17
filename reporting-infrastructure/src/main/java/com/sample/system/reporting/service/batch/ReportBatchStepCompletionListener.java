package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportFileEntity;
import com.sample.system.reporting.service.dataaccess.repository.ReportFileRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import com.sample.system.reporting.service.reportformat.ReportFormatResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListener;
import com.sample.system.reporting.service.application.ports.output.ReportFileStoragePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class ReportBatchStepCompletionListener implements StepExecutionListener {

    private final ReportBatchSharedStateRegistry sharedStateRegistry;
    private final ReportFileRepository reportFileRepository;
    private final ReportFileStoragePort storagePort;

    public ReportBatchStepCompletionListener(ReportBatchSharedStateRegistry sharedStateRegistry,
                                             ReportFileRepository reportFileRepository,
                                             ReportFileStoragePort storagePort) {
        this.sharedStateRegistry = sharedStateRegistry;
        this.reportFileRepository = reportFileRepository;
        this.storagePort = storagePort;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        Long reportExecutionId = stepExecution.getJobParameters().getLong("reportExecutionId");
        log.info("[BatchStep] Starting. stepName={}, executionId={}, chunkSize={}",
                stepExecution.getStepName(),
                reportExecutionId,
                stepExecution.getJobParameters().getLong("chunkSize"));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Long reportExecutionId = stepExecution.getJobParameters().getLong("reportExecutionId");
        if (reportExecutionId == null) {
            log.warn("Skipping report step completion. reportExecutionId job parameter is missing. stepName={}",
                    stepExecution.getStepName());
            return stepExecution.getExitStatus();
        }

        ConcurrentMap<String, Object> sharedState = sharedStateRegistry.get(reportExecutionId);
        logPipelineSummary(reportExecutionId, sharedState, stepExecution);
        if (sharedState == null || !Boolean.TRUE.equals(sharedState.get(ReportBatchSharedStateKeys.EXPORT_MODE))) {
            log.debug("Skipping report export completion. executionId={}, sharedStatePresent={}, exportMode={}",
                    reportExecutionId,
                    sharedState != null,
                    sharedState != null ? sharedState.get(ReportBatchSharedStateKeys.EXPORT_MODE) : null);
            return stepExecution.getExitStatus();
        }

        ReportDefinition definition =
                (ReportDefinition) sharedState.get(ReportBatchSharedStateKeys.REPORT_DEFINITION);
        ReportExecution execution =
                (ReportExecution) sharedState.get(ReportBatchSharedStateKeys.REPORT_EXECUTION);
        ReportFormat format = (ReportFormat) sharedState.get(ReportBatchSharedStateKeys.REPORT_FORMAT);
        @SuppressWarnings("unchecked")
        Map<String, Object> parameters =
                (Map<String, Object>) sharedState.get(ReportBatchSharedStateKeys.PARAMETERS);

        if (definition == null || execution == null || format == null) {
            log.warn("Cannot complete report export because shared state is incomplete. executionId={}, definitionPresent={}, executionPresent={}, format={}",
                    reportExecutionId,
                    definition != null,
                    execution != null,
                    format);
            return stepExecution.getExitStatus();
        }

        ReportTemplate template =
                (ReportTemplate) sharedState.get(ReportBatchSharedStateKeys.REPORT_TEMPLATE);
        try {
            log.info("Finalizing streamed report export. executionId={}, reportCode={}, format={}, parameterCount={}, templatePresent={}",
                    reportExecutionId,
                    definition.getReportCode(),
                    format,
                    parameters != null ? parameters.size() : 0,
                    template != null);

            ReportFormatResult result = streamingResult(sharedState);
            if (result == null) {
                log.warn("Cannot complete report export because streaming result is missing. executionId={}, reportCode={}, format={}",
                        reportExecutionId,
                        definition.getReportCode(),
                        format);
                return stepExecution.getExitStatus();
            }

            ReportFileEntity savedFile = storeResult(result);
            execution.setGeneratedFileId(savedFile.getId());
            log.info("Streamed report export completed. executionId={}, reportCode={}, fileId={}, fileName={}, fileSize={}, objectName={}",
                    reportExecutionId,
                    definition.getReportCode(),
                    savedFile.getId(),
                    savedFile.getFileName(),
                    savedFile.getFileSize(),
                    savedFile.getObjectName());
            return stepExecution.getExitStatus();
        } catch (RuntimeException ex) {
            log.error("Report export completion failed. executionId={}, reportCode={}, format={}, message={}",
                    reportExecutionId,
                    definition.getReportCode(),
                    format,
                    ex.getMessage(),
                    ex);
            stepExecution.addFailureException(ex);
            return ExitStatus.FAILED.addExitDescription(ex.getMessage());
        }
    }

    private void logPipelineSummary(Long reportExecutionId,
                                    ConcurrentMap<String, Object> sharedState,
                                    StepExecution stepExecution) {
        if (sharedState == null) {
            log.info("[BatchStep] Completed without shared state. executionId={}, stepName={}, status={}",
                    reportExecutionId,
                    stepExecution.getStepName(),
                    stepExecution.getStatus());
            return;
        }
        ReportDefinition definition = (ReportDefinition) sharedState.get(ReportBatchSharedStateKeys.REPORT_DEFINITION);
        ReportFormat format = (ReportFormat) sharedState.get(ReportBatchSharedStateKeys.REPORT_FORMAT);
        log.info("[BatchStep] Pipeline summary. executionId={}, reportCode={}, format={}, readRows={}, processedRows={}, writtenRows={}, exportMode={}, stepStatus={}",
                reportExecutionId,
                definition != null ? definition.getReportCode() : null,
                format,
                countValue(sharedState.get(ReportBatchSharedStateKeys.READ_ROW_COUNT)),
                countValue(sharedState.get(ReportBatchSharedStateKeys.PROCESSED_ROW_COUNT)),
                countValue(sharedState.get(ReportBatchSharedStateKeys.WRITTEN_ROW_COUNT)),
                sharedState.get(ReportBatchSharedStateKeys.EXPORT_MODE),
                stepExecution.getStatus());
    }

    private long countValue(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private ReportFormatResult streamingResult(ConcurrentMap<String, Object> sharedState) {
        Object result = sharedState.get(ReportBatchSharedStateKeys.STREAMING_RESULT);
        if (result instanceof ReportFormatResult reportFormatResult) {
            return reportFormatResult;
        }
        Object resource = sharedState.get(ReportBatchSharedStateKeys.STREAMING_RESOURCE);
        if (resource instanceof StreamingReportResource streamingResource) {
            ReportFormatResult reportFormatResult = streamingResource.closeAndGetResult();
            sharedState.put(ReportBatchSharedStateKeys.STREAMING_RESULT, reportFormatResult);
            return reportFormatResult;
        }
        return null;
    }

    private ReportFileEntity storeResult(ReportFormatResult result) {
        ReportFileEntity entity = new ReportFileEntity();
        entity.setFileName(result.getFileName());
        entity.setOriginalFileName(result.getFileName());
        entity.setFileExtension(result.getFileExtension());
        entity.setFileSize(result.getFileSize());
        entity.setBucketName(storagePort.bucketName());
        entity.setObjectName(result.getObjectName());
        entity.setContentType(result.getContentType());
        entity.setChecksum(sha256(result));
        storagePort.store(result.getObjectName(), result.getFile(), result.getContentType());
        entity.setStorageProvider(storagePort.providerName());
        return reportFileRepository.save(entity);
    }

    private String sha256(ReportFormatResult result) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream inputStream = Files.newInputStream(result.getFile())) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Cannot calculate report file checksum: " + ex.getMessage());
        }
    }
}
