package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IValidationRuleDefinitionRepository;
import com.sample.system.reporting.service.dataaccess.mapper.ValidationRuleDefinitionDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ValidationRuleDefinitionRepository;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationRuleDefinitionPersistenceAdapter implements IValidationRuleDefinitionRepository {

    private final ValidationRuleDefinitionRepository validationRuleDefinitionRepository;
    private final ValidationRuleDefinitionDataAccessMapper validationRuleDefinitionDataAccessMapper;

    @Override
    public ValidationRuleDefinition save(ValidationRuleDefinition validationRuleDefinition) {
        return validationRuleDefinitionDataAccessMapper.validationRuleDefinitionEntityToValidationRuleDefinition(
                validationRuleDefinitionRepository.save(
                        validationRuleDefinitionDataAccessMapper
                                .validationRuleDefinitionToValidationRuleDefinitionEntity(validationRuleDefinition)));
    }

    @Override
    public ValidationRuleDefinition findById(Long id) {
        return validationRuleDefinitionRepository.findById(id)
                .map(validationRuleDefinitionDataAccessMapper::validationRuleDefinitionEntityToValidationRuleDefinition)
                .orElse(null);
    }

    @Override
    public List<ValidationRuleDefinition> findByReportDefinitionId(Long reportDefinitionId) {
        return validationRuleDefinitionRepository
                .findByReportDefinition_IdOrderByExecutionOrderAsc(reportDefinitionId)
                .stream()
                .map(validationRuleDefinitionDataAccessMapper::validationRuleDefinitionEntityToValidationRuleDefinition)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        validationRuleDefinitionRepository.deleteById(id);
    }
}
