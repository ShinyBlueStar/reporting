package com.sample.system.reporting.service.dataaccess.scheduler;

import com.sample.system.reporting.service.application.ports.input.ReportExecutionService;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportScheduleEntity;
import com.sample.system.reporting.service.dataaccess.repository.ReportDefinitionRepository;
import com.sample.system.reporting.service.dataaccess.repository.ReportScheduleRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportScheduleExecutionJobTest {

    private final ReportScheduleRepository schedules = mock(ReportScheduleRepository.class);
    private final ReportDefinitionRepository definitions = mock(ReportDefinitionRepository.class);
    private final ReportExecutionService executionService = mock(ReportExecutionService.class);
    private final ReportScheduleExecutionJob job =
            new ReportScheduleExecutionJob(schedules, definitions, executionService);

    private ReportScheduleEntity schedule(long id, String cron, Instant next) {
        ReportScheduleEntity s = new ReportScheduleEntity();
        s.setId(id);
        s.setCronExpression(cron);
        s.setNextExecutionTime(next);
        s.setScheduleName("s" + id);
        ReportDefinitionEntity d = new ReportDefinitionEntity();
        d.setId(10L);
        d.setActive(true);
        d.setReportCode("X");
        s.setReportDefinition(d);
        return s;
    }

    @Test
    void invalidCronOfOneScheduleDoesNotBlockOthers() {
        ReportScheduleEntity broken = schedule(1, "not a cron", Instant.now().minusSeconds(5));
        ReportScheduleEntity good = schedule(2, "0 0 * * * *", Instant.now().minusSeconds(5));
        when(schedules.findByActiveTrue()).thenReturn(List.of(broken, good));
        when(schedules.claimDueSchedule(eq(2L), any(), any())).thenReturn(1);
        when(definitions.findById(10L)).thenReturn(Optional.of(good.getReportDefinition()));

        job.executeDueSchedules();

        verify(executionService, times(1)).executeReport(any());
    }

    @Test
    void scheduleClaimedByAnotherInstanceIsNotExecuted() {
        ReportScheduleEntity due = schedule(1, "0 0 * * * *", Instant.now().minusSeconds(5));
        when(schedules.findByActiveTrue()).thenReturn(List.of(due));
        when(schedules.claimDueSchedule(eq(1L), any(), any())).thenReturn(0);

        job.executeDueSchedules();

        verify(executionService, never()).executeReport(any());
    }

    @Test
    void notYetDueScheduleIsSkipped() {
        ReportScheduleEntity later = schedule(1, "0 0 * * * *", Instant.now().plusSeconds(3600));
        when(schedules.findByActiveTrue()).thenReturn(List.of(later));

        job.executeDueSchedules();

        verify(schedules, never()).claimDueSchedule(any(), any(), any());
        verify(executionService, never()).executeReport(any());
    }
}
