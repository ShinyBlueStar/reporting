package com.sample.system.reporting.service.application.response.validationrule;

import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationRuleDefinitionResponse {
    private final Long id;
    private final Long reportDefinitionId;
    private final ValidationRuleType validationRuleType;
    private final String configuration;
    private final String errorCode;
    private final String errorMessage;
    private final Integer executionOrder;
    private final Boolean enabled;
}
