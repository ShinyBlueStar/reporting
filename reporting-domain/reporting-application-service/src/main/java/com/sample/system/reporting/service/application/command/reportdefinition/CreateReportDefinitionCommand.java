package com.sample.system.reporting.service.application.command.reportdefinition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateReportDefinitionCommand {

    @NotBlank
    private String reportDefinitionCode;

    @NotBlank
    private String reportName;

    private String reportDescription;
    private Long categoryId;
    private String sqlQuery;
    private Integer timeoutSeconds;
    private Boolean active;
    private Long version;
    private String reportType;

    @Positive
    private Long maxExportRows;
}
