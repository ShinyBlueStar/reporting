package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportExecutionDetailEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportExecutionDetail;
import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionDetailId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportExecutionDetailId.class)
public interface ReportExecutionDetailDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportExecutionDetailId(entity.getId()) : null)")
    ReportExecutionDetail reportExecutionDetailEntityToReportExecutionDetail(ReportExecutionDetailEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ReportExecutionDetailEntity reportExecutionDetailToReportExecutionDetailEntity(ReportExecutionDetail model);
}
