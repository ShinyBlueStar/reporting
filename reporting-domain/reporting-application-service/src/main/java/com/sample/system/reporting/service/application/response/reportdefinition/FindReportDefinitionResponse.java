package com.sample.system.reporting.service.application.response.reportdefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FindReportDefinitionResponse {

    private final Long reportDefinitionId;
    private final String reportDefinitionCode;
    private final String reportName;
    private final String reportDescription;
    private final Long categoryId;
    private final String categoryName;
    @JsonIgnore
    private final String sqlQuery;
    private final Integer timeoutSeconds;
    private final Boolean active;
    private final Long version;
    private final String reportType;
    private final Long maxExportRows;
}
