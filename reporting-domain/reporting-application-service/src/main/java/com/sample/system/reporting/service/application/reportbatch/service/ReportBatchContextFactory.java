package com.sample.system.reporting.service.application.reportbatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.ports.output.IReportExecutionRepository;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchJobParameterKeys;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.application.utility.ReportParameterJsonValues;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ReportBatchContextFactory {

    private final ReportDefinitionService reportDefinitionService;
    private final IReportExecutionRepository reportExecutionRepository;
    private final ObjectMapper objectMapper;

    public ReportBatchContextFactory(ReportDefinitionService reportDefinitionService,
                                     IReportExecutionRepository reportExecutionRepository,
                                     ObjectMapper objectMapper) {
        this.reportDefinitionService = reportDefinitionService;
        this.reportExecutionRepository = reportExecutionRepository;
        this.objectMapper = objectMapper;
    }

    public ReportBatchContext create(ConcurrentMap<String, Object> jobParameters) {
        Long reportDefinitionId = asLong(jobParameters, ReportBatchJobParameterKeys.REPORT_DEFINITION_ID);
        Long reportExecutionId = asLong(jobParameters, ReportBatchJobParameterKeys.REPORT_EXECUTION_ID);
        String reportFormat = asString(jobParameters, ReportBatchJobParameterKeys.REPORT_FORMAT);
        String parametersJson = asString(jobParameters, ReportBatchJobParameterKeys.PARAMETERS_JSON);
        int chunkSize = asInt(jobParameters, ReportBatchJobParameterKeys.CHUNK_SIZE);
        boolean exportMode = asBoolean(jobParameters, ReportBatchJobParameterKeys.EXPORT_MODE);

        ReportDefinition definition = reportDefinitionService.findReportDefinitionById(reportDefinitionId);
        ReportExecution execution = reportExecutionRepository.findById(reportExecutionId);
        if (execution == null) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_NOT_FOUND.getCode(),
                    ErrorCode.REPORT_EXECUTION_NOT_FOUND.name());
        }
        ConcurrentHashMap<String, Object> sharedState = new ConcurrentHashMap<>();
        Map<String, Object> parameters = parameters(parametersJson);
        sharedState.put(ReportBatchSharedStateKeys.EXPORT_MODE, exportMode);
        sharedState.put(ReportBatchSharedStateKeys.REPORT_DEFINITION, definition);
        sharedState.put(ReportBatchSharedStateKeys.REPORT_EXECUTION, execution);
        sharedState.put(ReportBatchSharedStateKeys.REPORT_FORMAT, ReportFormat.from(reportFormat));
        sharedState.put(ReportBatchSharedStateKeys.PARAMETERS, parameters);
        return new ReportBatchContext(
                definition,
                execution,
                ReportFormat.from(reportFormat),
                parameters,
                chunkSize,
                exportMode,
                sharedState);
    }

    private Long asLong(ConcurrentMap<String, Object> jobParameters, String key) {
        Object value = jobParameters.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return value != null ? Long.valueOf(value.toString()) : null;
    }

    private int asInt(ConcurrentMap<String, Object> jobParameters, String key) {
        Long value = asLong(jobParameters, key);
        return value != null ? Math.toIntExact(value) : 0;
    }

    private String asString(ConcurrentMap<String, Object> jobParameters, String key) {
        Object value = jobParameters.get(key);
        return value != null ? value.toString() : null;
    }

    private boolean asBoolean(ConcurrentMap<String, Object> jobParameters, String key) {
        Object value = jobParameters.get(key);
        return value != null && Boolean.parseBoolean(value.toString());
    }

    private Map<String, Object> parameters(String parametersJson) {
        if (parametersJson == null || parametersJson.isBlank()) {
            return Map.of();
        }
        try {
            JsonNode root = objectMapper.readTree(parametersJson);
            return ReportParameterJsonValues.toMap(root);
        } catch (Exception ex) {
            throw new ReportingDomainException(
                    ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                    "Invalid batch report parameters");
        }
    }
}
