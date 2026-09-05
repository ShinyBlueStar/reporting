package com.sample.system.reporting.service.application.command.reportexecution;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ExecuteReportCommand {

    @NotNull
    private Long reportDefinitionId;

    private String requestedBy;

    private String reportFormat;

    @JsonDeserialize(using = ReportParameterMapDeserializer.class)
    private Map<String, Object> parameters = new LinkedHashMap<>();
}
