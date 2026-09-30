package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.PartyReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.PartyReport;
import com.sample.system.reporting.service.domain.model.valueObject.PartyReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = PartyReportId.class)
public interface PartyReportDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new PartyReportId(entity.getId()) : null)")
    PartyReport partyReportEntityToPartyReport(PartyReportEntityQuery entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    PartyReportEntityQuery partyReportToPartyReportEntity(PartyReport model);
}
