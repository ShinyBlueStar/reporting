package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import com.sample.system.reporting.service.domain.model.valueObject.ValidationRuleDefinitionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRuleDefinition extends BaseEntity<ValidationRuleDefinitionId> {

    private Long reportDefinitionId;
    private ValidationRuleType validationRuleType;
    private String configuration;
    private String errorCode;
    private String errorMessage;
    private Integer executionOrder;
    private Boolean enabled;
}
