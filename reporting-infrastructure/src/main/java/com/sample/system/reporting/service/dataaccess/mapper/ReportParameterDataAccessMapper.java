package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportParameterEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import com.sample.system.reporting.service.domain.model.valueObject.ReportParameterId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportParameterId.class)
public interface ReportParameterDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportParameterId(entity.getId()) : null)")
    @Mapping(target = "reportDefinitionId", source = "reportDefinition.id")
    ReportParameter reportParameterEntityToReportParameter(ReportParameterEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    @Mapping(target = "reportDefinition",
            expression = "java(toReportDefinitionEntity(model.getReportDefinitionId()))")
    ReportParameterEntity reportParameterToReportParameterEntity(ReportParameter model);

    default ReportDefinitionEntity toReportDefinitionEntity(Long reportDefinitionId) {
        if (reportDefinitionId == null) {
            return null;
        }
        ReportDefinitionEntity definition = new ReportDefinitionEntity();
        definition.setId(reportDefinitionId);
        return definition;
    }
}
