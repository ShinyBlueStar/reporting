package com.sample.system.reporting.service.application.handler.validationrule;

import com.sample.system.reporting.service.application.command.common.FindByIdCommand;
import com.sample.system.reporting.service.application.command.common.FindByReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.validationrule.CreateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.command.validationrule.UpdateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.mapper.ValidationRuleDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ValidationRuleDefinitionService;
import com.sample.system.reporting.service.application.response.common.MutationResponse;
import com.sample.system.reporting.service.application.response.validationrule.ValidationRuleDefinitionResponse;
import com.sample.system.reporting.service.application.util.JsonStructureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationRuleDefinitionCommandHandler {
    private final ValidationRuleDefinitionService service;
    private final ValidationRuleDefinitionMapper mapper;
    private final JsonStructureValidator jsonValidator;

    public MutationResponse create(CreateValidationRuleDefinitionCommand command) {
        jsonValidator.requireValidJson(command.getConfiguration());
        var saved = service.create(mapper.from(command));
        return new MutationResponse(saved.getId().getValue(), "Validation rule created successfully");
    }
    public MutationResponse update(UpdateValidationRuleDefinitionCommand command) {
        jsonValidator.requireValidJson(command.getConfiguration());
        var saved = service.update(mapper.from(command));
        return new MutationResponse(saved.getId().getValue(), "Validation rule updated successfully");
    }
    public ValidationRuleDefinitionResponse find(FindByIdCommand command) {
        return mapper.toResponse(service.findById(command.getId()));
    }
    public List<ValidationRuleDefinitionResponse> search(FindByReportDefinitionCommand command) {
        return service.findByReportDefinitionId(command.getReportDefinitionId()).stream()
                .map(mapper::toResponse).toList();
    }
    public MutationResponse delete(FindByIdCommand command) {
        service.delete(command.getId());
        return new MutationResponse(command.getId(), "Validation rule deleted successfully");
    }
}
