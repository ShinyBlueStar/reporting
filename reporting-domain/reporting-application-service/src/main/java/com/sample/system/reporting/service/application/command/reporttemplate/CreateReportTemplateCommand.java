package com.sample.system.reporting.service.application.command.reporttemplate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateReportTemplateCommand {
    @NotNull private Long reportDefinitionId;
    @NotBlank private String templateName;
    @NotBlank private String sheetName;
    @NotBlank private String columnsJson;
}
