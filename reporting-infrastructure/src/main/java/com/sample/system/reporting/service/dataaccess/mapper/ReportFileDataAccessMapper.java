package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportFileEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import com.sample.system.reporting.service.domain.model.valueObject.ReportFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportFileId.class)
public interface ReportFileDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportFileId(entity.getId()) : null)")
    ReportFile reportFileEntityToReportFile(ReportFileEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ReportFileEntity reportFileToReportFileEntity(ReportFile model);
}
