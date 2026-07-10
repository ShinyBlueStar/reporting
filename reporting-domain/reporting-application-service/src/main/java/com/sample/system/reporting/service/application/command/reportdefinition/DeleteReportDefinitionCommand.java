package com.sample.system.reporting.service.application.command.reportdefinition;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeleteReportDefinitionCommand {

    @NotNull
    private Long reportDefinitionId;
}
