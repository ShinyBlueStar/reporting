package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportTemplateId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportTemplate extends BaseEntity<ReportTemplateId> {

    private Long reportDefinitionId;
    private String templateName;
    private String sheetName;
    private String columnsJson;
}
