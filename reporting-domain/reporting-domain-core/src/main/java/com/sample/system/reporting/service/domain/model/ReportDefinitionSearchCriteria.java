package com.sample.system.reporting.service.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportDefinitionSearchCriteria {

    private String reportCode;
    private String reportName;
    private Long categoryId;
    private String categoryName;
    private String reportType;
    private Boolean active;
    private Integer page;
    private Integer size;
}
