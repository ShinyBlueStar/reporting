package com.sample.system.reporting.service.reportformat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReportTemplateColumnResolver {

    private final ObjectMapper objectMapper;

    public ReportTemplateColumnResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ReportFormatColumn> resolveColumns(ReportTemplate template, Map<String, Object> sampleRow) {
        if (template != null && template.getColumnsJson() != null && !template.getColumnsJson().isBlank()) {
            try {
                return objectMapper.readValue(
                        template.getColumnsJson(),
                        new TypeReference<List<ReportFormatColumn>>() {
                        });
            } catch (JsonProcessingException ex) {
                throw new ReportingDomainException(
                        ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                        "Invalid report template columns");
            }
        }

        if (sampleRow == null || sampleRow.isEmpty()) {
            return List.of();
        }
        return sampleRow.keySet().stream()
                .map(this::toColumn)
                .toList();
    }

    private ReportFormatColumn toColumn(String field) {
        ReportFormatColumn column = new ReportFormatColumn();
        column.setField(field);
        column.setTitle(field);
        return column;
    }
}
