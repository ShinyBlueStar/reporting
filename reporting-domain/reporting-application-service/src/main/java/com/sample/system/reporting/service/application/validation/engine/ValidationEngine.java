package com.sample.system.reporting.service.application.validation.engine;

import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;

import java.util.List;

public interface ValidationEngine {

    void validate(ValidationContext context, List<ValidationRuleDefinition> rules);
}
