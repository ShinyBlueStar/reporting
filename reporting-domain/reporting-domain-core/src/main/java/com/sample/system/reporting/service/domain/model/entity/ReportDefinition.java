package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportDefinitionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDefinition extends AggregateRoot<ReportDefinitionId> {

    private String reportCode;
    private String reportName;
    private String reportDescription;
    private Long categoryId;
    private String categoryName;
    private String sqlQuery;
    private Integer timeoutSeconds;
    private Boolean active;
    private Long version;
    private String reportType;
    private Long maxExportRows;
}
