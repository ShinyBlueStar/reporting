package com.sample.system.reporting.service.application.command.reportdefinition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindReportDefinitionCommand {
    private Long reportDefinitionId;
    private String reportDefinitionCode;
}
