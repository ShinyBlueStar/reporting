package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.ReportEventEntity;
import com.sample.system.reporting.service.domain.model.entity.ReportEvent;
import com.sample.system.reporting.service.domain.model.valueObject.ReportEventId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportEventId.class)
public interface ReportEventDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ReportEventId(entity.getId()) : null)")
    ReportEvent reportEventEntityToReportEvent(ReportEventEntity entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ReportEventEntity reportEventToReportEventEntity(ReportEvent model);
}
