package com.sample.system.reporting.service.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.ports.output.ReportBatchLauncherPort;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchJobParameterKeys;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchLaunchRequest;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class BatchReportLauncherAdapter implements ReportBatchLauncherPort {

    private final JobLauncher jobLauncher;
    private final Job reportExecutionBatchJob;
    private final ObjectMapper objectMapper;
    private final ReportBatchSharedStateRegistry sharedStateRegistry;

    public BatchReportLauncherAdapter(JobLauncher jobLauncher,
                                      Job reportExecutionBatchJob,
                                      ObjectMapper objectMapper,
                                      ReportBatchSharedStateRegistry sharedStateRegistry) {
        this.jobLauncher = jobLauncher;
        this.reportExecutionBatchJob = reportExecutionBatchJob;
        this.objectMapper = objectMapper;
        this.sharedStateRegistry = sharedStateRegistry;
    }

    @Override
    public ConcurrentMap<String, Object> launch(ReportBatchLaunchRequest request) {
        Long reportExecutionId = request.getReportExecution().getId().getValue();
        try {
            log.info("Launching report batch job. executionId={}, reportDefinitionId={}, reportCode={}, format={}, exportMode={}, chunkSize={}",
                    reportExecutionId,
                    request.getReportDefinition().getId().getValue(),
                    request.getReportDefinition().getReportCode(),
                    request.getReportFormat(),
                    request.isExportMode(),
                    request.getChunkSize());
            JobParameters parameters = new JobParametersBuilder()
                    .addLong(ReportBatchJobParameterKeys.REPORT_DEFINITION_ID,
                            request.getReportDefinition().getId().getValue())
                    .addLong(ReportBatchJobParameterKeys.REPORT_EXECUTION_ID, reportExecutionId)
                    .addString(ReportBatchJobParameterKeys.REPORT_FORMAT, request.getReportFormat().name())
                    .addString(ReportBatchJobParameterKeys.PARAMETERS_JSON,
                            objectMapper.writeValueAsString(request.getParameters()))
                    .addLong(ReportBatchJobParameterKeys.CHUNK_SIZE, (long) request.getChunkSize())
                    .addString(ReportBatchJobParameterKeys.EXPORT_MODE, String.valueOf(request.isExportMode()))
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(reportExecutionBatchJob, parameters);
            log.info("Report batch job finished. executionId={}, jobExecutionId={}, status={}, exitStatus={}",
                    reportExecutionId,
                    jobExecution.getId(),
                    jobExecution.getStatus(),
                    jobExecution.getExitStatus());
            if (isUnsuccessful(jobExecution)) {
                throw new ReportingDomainException(
                        ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                        failureMessage(jobExecution));
            }
            ConcurrentMap<String, Object> sharedState = sharedStateRegistry.release(reportExecutionId);
            if (sharedState == null) {
                log.error("Report batch shared state missing after job. executionId={}, status={}",
                        reportExecutionId,
                        jobExecution.getStatus());
                throw new ReportingDomainException(
                        ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                        "Report batch shared state was not available after job execution");
            }
            log.info("[BatchJob] Pipeline totals. executionId={}, readRows={}, processedRows={}, writtenRows={}, exportMode={}",
                    reportExecutionId,
                    countValue(sharedState.get(ReportBatchSharedStateKeys.READ_ROW_COUNT)),
                    countValue(sharedState.get(ReportBatchSharedStateKeys.PROCESSED_ROW_COUNT)),
                    countValue(sharedState.get(ReportBatchSharedStateKeys.WRITTEN_ROW_COUNT)),
                    sharedState.get(ReportBatchSharedStateKeys.EXPORT_MODE));
            log.debug("Released report batch shared state. executionId={}, keys={}", reportExecutionId, sharedState.keySet());
            return sharedState;
        } catch (ReportingDomainException ex) {
            log.error("Report batch launch failed. executionId={}, message={}", reportExecutionId, ex.getMessage(), ex);
            sharedStateRegistry.release(reportExecutionId);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected report batch launch failure. executionId={}, message={}", reportExecutionId, ex.getMessage(), ex);
            sharedStateRegistry.release(reportExecutionId);
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    ex.getMessage() != null ? ex.getMessage() : ErrorCode.REPORT_EXECUTION_FAILED.name());
        }
    }

    private long countValue(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private boolean isUnsuccessful(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            return true;
        }
        ExitStatus exitStatus = jobExecution.getExitStatus();
        return exitStatus != null && ExitStatus.FAILED.getExitCode().equals(exitStatus.getExitCode());
    }

    private String failureMessage(JobExecution jobExecution) {
        String stepFailure = jobExecution.getStepExecutions().stream()
                .map(step -> step.getExitStatus() != null ? step.getExitStatus().getExitDescription() : null)
                .filter(message -> message != null && !message.isBlank())
                .findFirst()
                .orElse(null);
        if (stepFailure != null) {
            return stepFailure;
        }
        return jobExecution.getFailureExceptions().stream()
                .map(Throwable::getMessage)
                .filter(message -> message != null && !message.isBlank())
                .findFirst()
                .orElse("Report batch execution failed with status: "
                        + jobExecution.getStatus()
                        + ", exitStatus: "
                        + jobExecution.getExitStatus());
    }
}
