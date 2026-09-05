package com.sample.system.reporting.service.application.reportbatch.model;

import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ReportBatchLaunchRequest {

    private final ReportDefinition reportDefinition;
    private final ReportExecution reportExecution;
    private final ReportFormat reportFormat;
    private final Map<String, Object> parameters;
    private final int chunkSize;
    private final boolean exportMode;
}
