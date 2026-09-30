package com.sample.system.reporting.service.dataaccess.mapper;

import com.sample.system.reporting.service.dataaccess.entity.query.ProductReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.ProductReport;
import com.sample.system.reporting.service.domain.model.valueObject.ProductReportId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ProductReportId.class)
public interface ProductReportDataAccessMapper {

    @Mapping(target = "id",
            expression = "java(entity.getId() != null ? new ProductReportId(entity.getId()) : null)")
    ProductReport productReportEntityToProductReport(ProductReportEntityQuery entity);

    @Mapping(target = "id",
            expression = "java(model.getId() != null ? model.getId().getValue() : null)")
    ProductReportEntityQuery productReportToProductReportEntity(ProductReport model);
}
