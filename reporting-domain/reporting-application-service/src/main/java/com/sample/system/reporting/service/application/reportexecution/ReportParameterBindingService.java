package com.sample.system.reporting.service.application.reportexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.application.utility.ReportInstantParser;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ReportParameterBindingService {

    private static final int PREVIEW_ROW_LIMIT = 50;

    private final ObjectMapper objectMapper;

    public int previewRowLimit() {
        return PREVIEW_ROW_LIMIT;
    }

    public Map<String, Object> resolveAndValidate(List<ReportParameter> definitions, Map<String, Object> input) {
        Map<String, Object> safeInput = input != null ? input : Map.of();
        Map<String, Object> resolved = new LinkedHashMap<>();

        for (ReportParameter definition : definitions) {
            String name = definition.getParameterName();
            Object value = safeInput.containsKey(name)
                    ? safeInput.get(name)
                    : definition.getDefaultValue();

            if (Boolean.TRUE.equals(definition.getRequired())
                    && isEmpty(value)) {
                throw new ReportingDomainException(
                        ErrorCode.REPORT_PARAMETER_REQUIRED.getCode(),
                        "Required parameter missing: " + name);
            }
            if (isEmpty(value)) {
                resolved.put(name, null);
                continue;
            }
            validateRegex(definition, value);
            resolved.put(name, coerceValue(definition.getParameterType(), value));
        }

        for (String key : safeInput.keySet()) {
            boolean known = definitions.stream()
                    .anyMatch(p -> p.getParameterName().equals(key));
            if (!known) {
                throw new ReportingDomainException(
                        ErrorCode.REPORT_PARAMETER_UNKNOWN.getCode(),
                        "Unknown parameter: " + key);
            }
        }
        return resolved;
    }

    public String toJson(Map<String, ?> parameters) {
        try {
            return objectMapper.writeValueAsString(parameters);
        } catch (JsonProcessingException ex) {
            throw new ReportingDomainException(
                    ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                    "Cannot serialize request parameters");
        }
    }

    private void validateRegex(ReportParameter definition, Object value) {
        if (definition.getValidationRegex() == null || definition.getValidationRegex().isBlank()) {
            return;
        }
        if (!Pattern.compile(definition.getValidationRegex()).matcher(value.toString()).matches()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_PARAMETER_INVALID.getCode(),
                    "Invalid value for parameter: " + definition.getParameterName());
        }
    }

    private Object coerceValue(String parameterType, Object value) {
        if (parameterType == null || parameterType.isBlank()) {
            return value;
        }
        return switch (parameterType.toUpperCase()) {
            case "NUMBER", "INTEGER", "LONG" -> value instanceof BigDecimal decimal
                    ? decimal
                    : new BigDecimal(value.toString().trim());
            case "BOOLEAN" -> value instanceof Boolean bool ? bool : Boolean.parseBoolean(value.toString());
            case "DATE", "DATETIME", "TIMESTAMP", "LOCALDATETIME", "INSTANT" -> ReportInstantParser.parseRequired(value);
            default -> value;
        };
    }

    private boolean isEmpty(Object value) {
        return value == null || value.toString().isBlank();
    }
}
