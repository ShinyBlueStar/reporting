package com.sample.system.reporting.service.application.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonStructureValidator {
    private final ObjectMapper objectMapper;

    public void requireValidJson(String json) {
        parse(json);
    }

    public void requireArray(String json) {
        if (!parse(json).isArray()) {
            throw invalidInput();
        }
    }

    private JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            throw invalidInput();
        }
    }

    private ReportingDomainException invalidInput() {
        return new ReportingDomainException(
                ErrorCode.INVALID_INPUT_PARAMETER.getCode(), ErrorCode.INVALID_INPUT_PARAMETER.name());
    }
}
