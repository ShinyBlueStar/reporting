package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.InstallmentReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.InstallmentReport;
import com.sample.system.reporting.service.domain.model.valueObject.InstallmentReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = InstallmentReportId.class)
public interface InstallmentReportDataAccessMapper {

    @Mapping(target = "loanAggregateId", source = "loan.aggregateId")
    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new InstallmentReportId(entity.getId()) : null)")
    InstallmentReport installmentReportEntityToInstallmentReport(InstallmentReportEntityQuery entity);

    @Mapping(target = "loan", ignore = true)
    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    InstallmentReportEntityQuery installmentReportToInstallmentReportEntity(InstallmentReport model);
}
