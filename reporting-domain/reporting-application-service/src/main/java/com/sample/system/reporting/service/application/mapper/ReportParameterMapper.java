package com.sample.system.reporting.service.application.mapper;

import com.sample.system.reporting.service.application.command.reportparameter.CreateReportParameterCommand;
import com.sample.system.reporting.service.application.command.reportparameter.UpdateReportParameterCommand;
import com.sample.system.reporting.service.application.response.reportparameter.ReportParameterResponse;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import com.sample.system.reporting.service.domain.model.valueObject.ReportParameterId;
import org.springframework.stereotype.Component;

@Component
public class ReportParameterMapper {
    public ReportParameter from(CreateReportParameterCommand command) {
        return map(command.getReportDefinitionId(), command.getParameterName(), command.getParameterLabel(),
                command.getParameterType(), command.getRequired(), command.getDefaultValue(),
                command.getValidationRegex(), command.getDisplayOrder(), command.getPlaceholder(),
                command.getHelpText(), command.getMaxLength(), command.getMinLength());
    }

    public ReportParameter from(UpdateReportParameterCommand command) {
        ReportParameter result = map(command.getReportDefinitionId(), command.getParameterName(),
                command.getParameterLabel(), command.getParameterType(), command.getRequired(),
                command.getDefaultValue(), command.getValidationRegex(), command.getDisplayOrder(),
                command.getPlaceholder(), command.getHelpText(), command.getMaxLength(), command.getMinLength());
        result.setId(new ReportParameterId(command.getId()));
        return result;
    }

    public ReportParameterResponse toResponse(ReportParameter value) {
        return new ReportParameterResponse(value.getId() == null ? null : value.getId().getValue(),
                value.getReportDefinitionId(), value.getParameterName(), value.getParameterLabel(),
                value.getParameterType(), value.getRequired(), value.getDefaultValue(), value.getValidationRegex(),
                value.getDisplayOrder(), value.getPlaceholder(), value.getHelpText(), value.getMaxLength(),
                value.getMinLength());
    }

    private ReportParameter map(Long reportId, String name, String label, String type, Boolean required,
                                String defaultValue, String regex, Integer order, String placeholder,
                                String helpText, Integer maxLength, Integer minLength) {
        return new ReportParameter(reportId, name, label, type, required != null ? required : false,
                defaultValue, regex, order != null ? order : 0, placeholder, helpText, maxLength, minLength);
    }
}
