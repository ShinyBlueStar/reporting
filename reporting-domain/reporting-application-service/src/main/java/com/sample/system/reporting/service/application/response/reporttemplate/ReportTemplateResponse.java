package com.sample.system.reporting.service.application.response.reporttemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportTemplateResponse {
    private final Long id;
    private final Long reportDefinitionId;
    private final String templateName;
    private final String sheetName;
    private final String columnsJson;
}
