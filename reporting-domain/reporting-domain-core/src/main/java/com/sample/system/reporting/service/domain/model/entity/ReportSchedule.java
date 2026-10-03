package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportScheduleId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportSchedule extends BaseEntity<ReportScheduleId> {

    private Long reportDefinitionId;
    private String scheduleName;
    private String cronExpression;
    private Boolean active;
    private String outputType;
    private Instant lastExecutionTime;
    private Instant nextExecutionTime;
    private String notifyEmail;
    private String scheduleParameters;
    private String lastStatus;
}
