package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.valueObject.ReportDefinitionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportDefinitionId.class)
public interface ReportDefinitionDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportDefinitionId(entity.getId()) : null)")
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "categoryName", source = "category")
    @Mapping(target = "reportType", source = "outputType")
    @Mapping(target = "version", expression = "java(entity.getVersion() != null ? entity.getVersion().longValue() : null)")
    ReportDefinition reportDefinitionEntityToReportDefinition(ReportDefinitionEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    @Mapping(target = "category", source = "categoryName")
    @Mapping(target = "outputType", source = "reportType")
    @Mapping(target = "version", expression = "java(model.getVersion() != null ? model.getVersion().intValue() : null)")
    ReportDefinitionEntity reportDefinitionToReportDefinitionEntity(ReportDefinition model);
}
