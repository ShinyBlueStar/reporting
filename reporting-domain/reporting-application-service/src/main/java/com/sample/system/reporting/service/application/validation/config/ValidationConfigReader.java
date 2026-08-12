package com.sample.system.reporting.service.application.validation.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.springframework.stereotype.Component;

@Component
public class ValidationConfigReader {

    private final ObjectMapper objectMapper;

    public ValidationConfigReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode read(String configuration) {
        if (configuration == null || configuration.isBlank()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(configuration);
        } catch (Exception ex) {
            throw new ReportingDomainException(
                    ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                    "Invalid validation rule configuration JSON");
        }
    }
}
