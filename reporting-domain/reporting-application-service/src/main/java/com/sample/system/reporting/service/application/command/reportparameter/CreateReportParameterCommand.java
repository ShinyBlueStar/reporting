package com.sample.system.reporting.service.application.command.reportparameter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateReportParameterCommand {
    @NotNull private Long reportDefinitionId;
    @NotBlank private String parameterName;
    @NotBlank private String parameterLabel;
    @NotBlank private String parameterType;
    private Boolean required;
    private String defaultValue;
    private String validationRegex;
    @PositiveOrZero private Integer displayOrder;
    private String placeholder;
    private String helpText;
    @PositiveOrZero private Integer maxLength;
    @PositiveOrZero private Integer minLength;
}
