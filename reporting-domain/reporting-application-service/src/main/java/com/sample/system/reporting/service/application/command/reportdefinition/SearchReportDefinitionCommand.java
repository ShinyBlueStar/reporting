package com.sample.system.reporting.service.application.command.reportdefinition;

import com.sample.system.reporting.service.application.command.common.PagedSearchCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchReportDefinitionCommand extends PagedSearchCommand {

    private String reportDefinitionCode;
    private String reportName;
    private Long categoryId;
    private String categoryName;
    private String reportType;
    private Boolean active;
}
