package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.AccountReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.AccountReport;
import com.sample.system.reporting.service.domain.model.valueObject.AccountReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = AccountReportId.class)
public interface AccountReportDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new AccountReportId(entity.getId()) : null)")
    AccountReport accountReportEntityToAccountReport(AccountReportEntityQuery entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    AccountReportEntityQuery accountReportToAccountReportEntity(AccountReport model);
}
