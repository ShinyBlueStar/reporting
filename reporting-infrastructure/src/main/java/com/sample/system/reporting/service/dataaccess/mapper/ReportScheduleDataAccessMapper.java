package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportScheduleEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportSchedule;
import com.sample.system.reporting.service.domain.model.valueObject.ReportScheduleId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportScheduleId.class)
public interface ReportScheduleDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportScheduleId(entity.getId()) : null)")
    ReportSchedule reportScheduleEntityToReportSchedule(ReportScheduleEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ReportScheduleEntity reportScheduleToReportScheduleEntity(ReportSchedule model);
}
