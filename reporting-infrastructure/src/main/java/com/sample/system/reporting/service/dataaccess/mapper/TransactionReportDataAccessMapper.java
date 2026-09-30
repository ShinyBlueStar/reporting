package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.TransactionReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.TransactionReport;
import com.sample.system.reporting.service.domain.model.valueObject.TransactionReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = TransactionReportId.class)
public interface TransactionReportDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new TransactionReportId(entity.getId()) : null)")
    TransactionReport transactionReportEntityToTransactionReport(TransactionReportEntityQuery entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    TransactionReportEntityQuery transactionReportToTransactionReportEntity(TransactionReport model);
}
