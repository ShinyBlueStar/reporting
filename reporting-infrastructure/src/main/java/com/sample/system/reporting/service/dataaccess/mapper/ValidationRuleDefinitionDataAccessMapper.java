package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ValidationRuleDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.valueObject.ValidationRuleDefinitionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ValidationRuleDefinitionId.class)
public interface ValidationRuleDefinitionDataAccessMapper {

    @Mapping(target = "reportDefinitionId", source = "reportDefinition.id")
    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ValidationRuleDefinitionId(entity.getId()) : null)")
    ValidationRuleDefinition validationRuleDefinitionEntityToValidationRuleDefinition(
            ValidationRuleDefinitionEntity entity);

    @Mapping(target = "reportDefinition",
            expression = "java(toReportDefinitionEntity(model.getReportDefinitionId()))")
    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ValidationRuleDefinitionEntity validationRuleDefinitionToValidationRuleDefinitionEntity(
            ValidationRuleDefinition model);

    default ReportDefinitionEntity toReportDefinitionEntity(Long reportDefinitionId) {
        if (reportDefinitionId == null) {
            return null;
        }
        ReportDefinitionEntity definition = new ReportDefinitionEntity();
        definition.setId(reportDefinitionId);
        return definition;
    }
}
