package com.sample.system.reporting.service.application.command.validationrule;

import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateValidationRuleDefinitionCommand {
    @NotNull private Long reportDefinitionId;
    @NotNull private ValidationRuleType validationRuleType;
    @NotBlank private String configuration;
    @NotBlank private String errorCode;
    @NotBlank private String errorMessage;
    @PositiveOrZero private Integer executionOrder;
    private Boolean enabled;
}
