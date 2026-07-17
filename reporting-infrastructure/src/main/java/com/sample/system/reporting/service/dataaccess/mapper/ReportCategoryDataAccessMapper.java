package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportCategoryEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportCategory;
import com.sample.system.reporting.service.domain.model.valueObject.ReportCategoryId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportCategoryId.class)
public interface ReportCategoryDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportCategoryId(entity.getId()) : null)")
    ReportCategory reportCategoryEntityToReportCategory(ReportCategoryEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ReportCategoryEntity reportCategoryToReportCategoryEntity(ReportCategory model);
}
