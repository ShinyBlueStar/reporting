package com.sample.system.reporting.service.application.mapper;

import com.sample.system.reporting.service.application.command.validationrule.CreateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.command.validationrule.UpdateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.response.validationrule.ValidationRuleDefinitionResponse;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.valueObject.ValidationRuleDefinitionId;
import org.springframework.stereotype.Component;

@Component
public class ValidationRuleDefinitionMapper {
    public ValidationRuleDefinition from(CreateValidationRuleDefinitionCommand command) {
        return map(command.getReportDefinitionId(), command.getValidationRuleType(), command.getConfiguration(),
                command.getErrorCode(), command.getErrorMessage(), command.getExecutionOrder(), command.getEnabled());
    }

    public ValidationRuleDefinition from(UpdateValidationRuleDefinitionCommand command) {
        ValidationRuleDefinition result = map(command.getReportDefinitionId(), command.getValidationRuleType(),
                command.getConfiguration(), command.getErrorCode(), command.getErrorMessage(),
                command.getExecutionOrder(), command.getEnabled());
        result.setId(new ValidationRuleDefinitionId(command.getId()));
        return result;
    }

    public ValidationRuleDefinitionResponse toResponse(ValidationRuleDefinition value) {
        return new ValidationRuleDefinitionResponse(value.getId() == null ? null : value.getId().getValue(),
                value.getReportDefinitionId(), value.getValidationRuleType(), value.getConfiguration(),
                value.getErrorCode(), value.getErrorMessage(), value.getExecutionOrder(), value.getEnabled());
    }

    private ValidationRuleDefinition map(Long reportId,
            com.sample.system.reporting.service.domain.model.enums.ValidationRuleType type,
            String configuration, String errorCode, String errorMessage, Integer order, Boolean enabled) {
        return new ValidationRuleDefinition(reportId, type, configuration, errorCode, errorMessage,
                order != null ? order : 0, enabled != null ? enabled : true);
    }
}
