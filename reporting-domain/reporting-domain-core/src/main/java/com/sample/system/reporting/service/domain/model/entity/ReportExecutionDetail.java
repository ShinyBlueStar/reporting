package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionDetailId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportExecutionDetail extends BaseEntity<ReportExecutionDetailId> {

    private Long reportExecutionId;
    private String stepName;
    private String stepStatus;
    private Instant startTime;
    private Instant endTime;
    private Long durationMs;
    private String message;
    private String errorMessage;
    private Integer stepOrder;
}
