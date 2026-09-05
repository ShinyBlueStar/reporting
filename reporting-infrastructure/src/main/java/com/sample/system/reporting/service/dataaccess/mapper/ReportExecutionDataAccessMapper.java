package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportExecutionEntity;
import com.sample.system.reporting.service.dataaccess.entity.command.ReportFileEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportExecutionId.class)
public interface ReportExecutionDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportExecutionId(entity.getId()) : null)")
    @Mapping(target = "reportDefinitionId",
            expression = "java(entity.getReportDefinition() != null ? entity.getReportDefinition().getId() : null)")
    @Mapping(target = "generatedFileId",
            expression = "java(entity.getGeneratedFile() != null ? entity.getGeneratedFile().getId() : null)")
    ReportExecution reportExecutionEntityToReportExecution(ReportExecutionEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    @Mapping(target = "reportDefinition",
            expression = "java(toReportDefinitionEntity(model.getReportDefinitionId()))")
    @Mapping(target = "generatedFile",
            expression = "java(toReportFileEntity(model.getGeneratedFileId()))")
    ReportExecutionEntity reportExecutionToReportExecutionEntity(ReportExecution model);

    default ReportDefinitionEntity toReportDefinitionEntity(Long reportDefinitionId) {
        if (reportDefinitionId == null) {
            return null;
        }
        ReportDefinitionEntity entity = new ReportDefinitionEntity();
        entity.setId(reportDefinitionId);
        return entity;
    }

    default ReportFileEntity toReportFileEntity(Long generatedFileId) {
        if (generatedFileId == null) {
            return null;
        }
        ReportFileEntity entity = new ReportFileEntity();
        entity.setId(generatedFileId);
        return entity;
    }
}
