package com.sample.system.reporting.service.application.rest.validationrule;

import com.sample.system.platform.commons.contracts.util.StandardResponse;
import com.sample.system.reporting.service.application.command.common.FindByIdCommand;
import com.sample.system.reporting.service.application.command.common.FindByReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.validationrule.CreateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.command.validationrule.UpdateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.handler.validationrule.ValidationRuleDefinitionCommandHandler;
import com.sample.system.reporting.service.application.response.common.MutationResponse;
import com.sample.system.reporting.service.application.response.validationrule.ValidationRuleDefinitionResponse;
import com.sample.system.reporting.service.application.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Validation Rule Definitions")
@RequestMapping("/api/v1/validation-rule-definition")
public class ValidationRuleDefinitionController {
    private final ValidationRuleDefinitionCommandHandler handler;

    @PostMapping
    public ResponseEntity<StandardResponse<MutationResponse>> create(
            @RequestBody @Valid CreateValidationRuleDefinitionCommand command) {
        return ResponseBuilder.created(handler.create(command), "Validation rule created successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<StandardResponse<MutationResponse>> update(
            @RequestBody @Valid UpdateValidationRuleDefinitionCommand command) {
        return ResponseBuilder.success(handler.update(command), "Validation rule updated successfully");
    }

    @PostMapping("/find")
    public ResponseEntity<StandardResponse<ValidationRuleDefinitionResponse>> find(
            @RequestBody @Valid FindByIdCommand command) {
        return ResponseBuilder.success(handler.find(command), "Validation rule retrieved successfully");
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse<List<ValidationRuleDefinitionResponse>>> search(
            @RequestBody @Valid FindByReportDefinitionCommand command) {
        return ResponseBuilder.success(handler.search(command), "Validation rules retrieved successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<StandardResponse<MutationResponse>> delete(
            @RequestBody @Valid FindByIdCommand command) {
        return ResponseBuilder.success(handler.delete(command), "Validation rule deleted successfully");
    }
}
