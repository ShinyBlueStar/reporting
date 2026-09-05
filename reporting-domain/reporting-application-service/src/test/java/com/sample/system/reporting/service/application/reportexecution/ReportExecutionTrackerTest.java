package com.sample.system.reporting.service.application.reportexecution;

import com.sample.system.reporting.service.application.ports.output.IReportExecutionDetailRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.entity.ReportExecutionDetail;
import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportExecutionTrackerTest {

    private final IReportExecutionDetailRepository repository = mock(IReportExecutionDetailRepository.class);
    private final ReportExecutionTracker tracker = new ReportExecutionTracker(repository);

    private ReportExecution execution() {
        ReportExecution execution = new ReportExecution();
        execution.setId(new ReportExecutionId(7L));
        return execution;
    }

    private List<ReportExecutionDetail> savedDetails(int expected) {
        ArgumentCaptor<ReportExecutionDetail> captor = ArgumentCaptor.forClass(ReportExecutionDetail.class);
        verify(repository, times(expected)).save(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void successfulStepRecordsRunningThenCompletedAndReturnsResult() {
        String result = tracker.executeStep(execution(), "QUERY", "running", "done", () -> "rows");

        assertEquals("rows", result);
        List<ReportExecutionDetail> details = savedDetails(2);
        assertEquals("RUNNING", details.get(0).getStepStatus());
        assertEquals("COMPLETED", details.get(1).getStepStatus());
        assertEquals(7L, details.get(1).getReportExecutionId());
        assertEquals("done", details.get(1).getMessage());
    }

    @Test
    void failingStepRecordsFailureAndRethrows() {
        IllegalStateException boom = new IllegalStateException("db down");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> tracker.executeStep(execution(), "QUERY", "running", "done", () -> {
                    throw boom;
                }));

        assertSame(boom, thrown);
        List<ReportExecutionDetail> details = savedDetails(2);
        assertEquals("FAILED", details.get(1).getStepStatus());
        assertEquals("db down", details.get(1).getMessage());
    }

    @Test
    void longMessagesAreTruncatedToColumnSize() {
        tracker.recordFailed(execution(), "QUERY", System.currentTimeMillis(), "x".repeat(5000));
        assertEquals(2000, savedDetails(1).get(0).getMessage().length());
    }
}
