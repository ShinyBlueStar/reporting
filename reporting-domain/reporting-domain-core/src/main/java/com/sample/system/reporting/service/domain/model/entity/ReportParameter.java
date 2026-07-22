package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportParameterId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportParameter extends BaseEntity<ReportParameterId> {

    private Long reportDefinitionId;
    private String parameterName;
    private String parameterLabel;
    private String parameterType;
    private Boolean required;
    private String defaultValue;
    private String validationRegex;
    private Integer displayOrder;
    private String placeholder;
    private String helpText;
    private Integer maxLength;
    private Integer minLength;
}
