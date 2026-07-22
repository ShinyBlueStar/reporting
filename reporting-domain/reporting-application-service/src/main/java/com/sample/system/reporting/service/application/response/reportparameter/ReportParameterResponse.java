package com.sample.system.reporting.service.application.response.reportparameter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportParameterResponse {
    private final Long id;
    private final Long reportDefinitionId;
    private final String parameterName;
    private final String parameterLabel;
    private final String parameterType;
    private final Boolean required;
    private final String defaultValue;
    private final String validationRegex;
    private final Integer displayOrder;
    private final String placeholder;
    private final String helpText;
    private final Integer maxLength;
    private final Integer minLength;
}
