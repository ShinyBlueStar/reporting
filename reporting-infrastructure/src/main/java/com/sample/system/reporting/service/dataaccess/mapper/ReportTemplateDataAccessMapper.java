package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportTemplateEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.domain.model.valueObject.ReportTemplateId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportTemplateId.class)
public interface ReportTemplateDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportTemplateId(entity.getId()) : null)")
    @Mapping(target = "reportDefinitionId", source = "reportDefinition.id")
    ReportTemplate reportTemplateEntityToReportTemplate(ReportTemplateEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    @Mapping(target = "reportDefinition",
            expression = "java(toReportDefinitionEntity(model.getReportDefinitionId()))")
    ReportTemplateEntity reportTemplateToReportTemplateEntity(ReportTemplate model);

    default ReportDefinitionEntity toReportDefinitionEntity(Long reportDefinitionId) {
        if (reportDefinitionId == null) {
            return null;
        }
        ReportDefinitionEntity reportDefinition = new ReportDefinitionEntity();
        reportDefinition.setId(reportDefinitionId);
        return reportDefinition;
    }
}
