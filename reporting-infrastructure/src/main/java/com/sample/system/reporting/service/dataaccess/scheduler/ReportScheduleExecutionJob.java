package com.sample.system.reporting.service.dataaccess.scheduler;

import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.application.ports.input.ReportExecutionService;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportScheduleEntity;
import com.sample.system.reporting.service.dataaccess.repository.ReportDefinitionRepository;
import com.sample.system.reporting.service.dataaccess.repository.ReportScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduleExecutionJob {

    private static final String LOAN_PORTFOLIO_REPORT_CODE = "LoanPortfolioReportService";

    private final ReportScheduleRepository reportScheduleRepository;
    private final ReportDefinitionRepository reportDefinitionRepository;
    private final ReportExecutionService reportExecutionService;

    @Scheduled(fixedDelayString = "${reporting.scheduler.fixed-delay-ms:60000}")
    public void executeDueSchedules() {
        // All schedule times are stored and compared in UTC.
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        for (ReportScheduleEntity schedule : reportScheduleRepository.findByActiveTrue()) {
            try {
                if (schedule.getNextExecutionTime() == null) {
                    updateNextExecution(schedule, now);
                    continue;
                }
                if (!isDue(schedule, now)) {
                    continue;
                }
                executeSchedule(schedule, now);
            } catch (RuntimeException ex) {
                // One broken schedule (e.g. invalid cron) must never block the others.
                log.error("Skipping schedule because it cannot be processed. scheduleId={}, cron={}",
                        schedule.getId(), schedule.getCronExpression(), ex);
            }
        }
    }

    private boolean isDue(ReportScheduleEntity schedule, LocalDateTime now) {
        return schedule.getNextExecutionTime() == null
                || !LocalDateTime.ofInstant(schedule.getNextExecutionTime(), ZoneOffset.UTC).isAfter(now);
    }

    private void executeSchedule(ReportScheduleEntity schedule, LocalDateTime now) {
        Instant next = nextExecutionInstant(schedule, now);
        if (reportScheduleRepository.claimDueSchedule(schedule.getId(), schedule.getNextExecutionTime(), next) == 0) {
            log.debug("Schedule already claimed by another instance. scheduleId={}", schedule.getId());
            return;
        }
        schedule.setNextExecutionTime(next);
        try {
            ReportDefinitionEntity definition = reportDefinitionRepository.findById(schedule.getReportDefinition().getId())
                    .orElse(null);
            if (definition == null || !Boolean.TRUE.equals(definition.getActive())) {
                return;
            }

            ExecuteReportCommand command = new ExecuteReportCommand();
            command.setReportDefinitionId(definition.getId());
            command.setRequestedBy("SCHEDULER:" + schedule.getScheduleName());
            command.setParameters(defaultParameters(definition));

            log.info("Executing scheduled report scheduleId={}, reportCode={}", schedule.getId(), definition.getReportCode());
            reportExecutionService.executeReport(command);

            schedule.setLastExecutionTime(now.toInstant(ZoneOffset.UTC));
            reportScheduleRepository.save(schedule);
        } catch (RuntimeException ex) {
            log.error("Scheduled report execution failed for scheduleId={}", schedule.getId(), ex);
        }
    }

    private Map<String, Object> defaultParameters(ReportDefinitionEntity definition) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        if (LOAN_PORTFOLIO_REPORT_CODE.equals(definition.getReportCode())) {
            LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
            Instant fromDate = yesterday.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toDate = yesterday.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
        }
        return parameters;
    }

    private void updateNextExecution(ReportScheduleEntity schedule, LocalDateTime from) {
        schedule.setNextExecutionTime(nextExecutionInstant(schedule, from));
        reportScheduleRepository.save(schedule);
    }

    private Instant nextExecutionInstant(ReportScheduleEntity schedule, LocalDateTime from) {
        LocalDateTime nextExecution = CronExpression.parse(schedule.getCronExpression()).next(from);
        return nextExecution == null ? null : nextExecution.toInstant(ZoneOffset.UTC);
    }
}
