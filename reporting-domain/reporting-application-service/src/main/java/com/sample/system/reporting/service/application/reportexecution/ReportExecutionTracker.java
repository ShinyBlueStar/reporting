package com.sample.system.reporting.service.application.reportexecution;

import com.sample.system.reporting.service.application.ports.output.IReportExecutionDetailRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.entity.ReportExecutionDetail;
import com.sample.system.reporting.service.domain.model.enums.ReportExecutionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ReportExecutionTracker {

    private final IReportExecutionDetailRepository reportExecutionDetailRepository;

    public void recordCompleted(ReportExecution execution, String stepName, long stepStart, String message) {
        recordStep(execution, stepName, ReportExecutionStatus.COMPLETED.name(), stepStart, message);
    }

    public void recordFailed(ReportExecution execution, String stepName, long stepStart, String message) {
        recordStep(execution, stepName, ReportExecutionStatus.FAILED.name(), stepStart, message);
    }

    public void executeStep(ReportExecution execution, String stepName, Runnable action) {
        executeStep(execution, stepName, "Running " + stepName, stepName + " completed", action);
    }

    public void executeStep(
            ReportExecution execution,
            String stepName,
            String runningMessage,
            String successMessage,
            Runnable action) {
        executeStep(execution, stepName, runningMessage, successMessage, () -> {
            action.run();
            return null;
        });
    }

    public <T> T executeStep(
            ReportExecution execution,
            String stepName,
            String runningMessage,
            String successMessage,
            Supplier<T> action) {
        long stepStart = System.currentTimeMillis();
        recordStep(execution, stepName, ReportExecutionStatus.RUNNING.name(), stepStart, runningMessage);
        try {
            T result = action.get();
            recordStep(execution, stepName, ReportExecutionStatus.COMPLETED.name(), stepStart, successMessage);
            return result;
        } catch (RuntimeException ex) {
            recordStep(execution, stepName, ReportExecutionStatus.FAILED.name(), stepStart, ex.getMessage());
            throw ex;
        }
    }

    private void recordStep(
            ReportExecution execution,
            String stepName,
            String stepStatus,
            long stepStart,
            String message) {
        ReportExecutionDetail detail = new ReportExecutionDetail();
        detail.setReportExecutionId(execution.getId().getValue());
        detail.setStepName(stepName);
        detail.setStepStatus(stepStatus);
        detail.setStartTime(Instant.ofEpochMilli(stepStart));
        detail.setEndTime(Instant.now());
        detail.setDurationMs(System.currentTimeMillis() - stepStart);
        detail.setMessage(truncate(message, 2000));
        reportExecutionDetailRepository.save(detail);
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
