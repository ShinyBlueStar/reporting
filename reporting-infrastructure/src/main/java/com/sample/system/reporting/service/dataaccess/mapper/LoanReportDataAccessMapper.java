package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.LoanReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.LoanReport;
import com.sample.system.reporting.service.domain.model.valueObject.LoanReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = LoanReportId.class)
public interface LoanReportDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new LoanReportId(entity.getId()) : null)")
    LoanReport loanReportEntityToLoanReport(LoanReportEntityQuery entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    LoanReportEntityQuery loanReportToLoanReportEntity(LoanReport model);
}
