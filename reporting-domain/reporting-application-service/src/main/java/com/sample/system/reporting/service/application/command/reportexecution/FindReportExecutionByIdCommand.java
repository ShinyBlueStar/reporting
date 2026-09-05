package com.sample.system.reporting.service.application.command.reportexecution;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindReportExecutionByIdCommand {

    @NotNull
    private Long reportExecutionCode;
}
