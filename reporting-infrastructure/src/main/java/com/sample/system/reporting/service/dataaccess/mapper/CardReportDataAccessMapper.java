package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.CardReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.CardReport;
import com.sample.system.reporting.service.domain.model.valueObject.CardReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = CardReportId.class)
public interface CardReportDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new CardReportId(entity.getId()) : null)")
    CardReport cardReportEntityToCardReport(CardReportEntityQuery entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    CardReportEntityQuery cardReportToCardReportEntity(CardReport model);
}
