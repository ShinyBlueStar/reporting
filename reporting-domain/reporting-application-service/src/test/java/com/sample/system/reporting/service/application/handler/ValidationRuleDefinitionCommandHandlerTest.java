package com.sample.system.reporting.service.application.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.command.validationrule.CreateValidationRuleDefinitionCommand;
import com.sample.system.reporting.service.application.handler.validationrule.ValidationRuleDefinitionCommandHandler;
import com.sample.system.reporting.service.application.mapper.ValidationRuleDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ValidationRuleDefinitionService;
import com.sample.system.reporting.service.application.util.JsonStructureValidator;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ValidationRuleDefinitionCommandHandlerTest {

    private final ValidationRuleDefinitionService service = mock(ValidationRuleDefinitionService.class);
    private final ValidationRuleDefinitionMapper mapper = mock(ValidationRuleDefinitionMapper.class);
    private final ValidationRuleDefinitionCommandHandler handler = new ValidationRuleDefinitionCommandHandler(
            service, mapper, new JsonStructureValidator(new ObjectMapper()));

    @Test
    void malformedConfigurationIsRejectedBeforeReachingTheService() {
        CreateValidationRuleDefinitionCommand command = new CreateValidationRuleDefinitionCommand();
        command.setConfiguration("{broken");

        assertThrows(ReportingDomainException.class, () -> handler.create(command));

        verifyNoInteractions(service);
    }
}
