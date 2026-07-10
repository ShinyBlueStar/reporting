package com.sample.system.reporting.service.application.response.reportdefinition;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteReportDefinitionResponse {

    private final Long reportDefinitionId;
    private final String message;
}
