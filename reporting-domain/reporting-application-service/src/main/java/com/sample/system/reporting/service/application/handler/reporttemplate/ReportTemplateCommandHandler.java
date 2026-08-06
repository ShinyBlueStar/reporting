package com.sample.system.reporting.service.application.handler.reporttemplate;

import com.sample.system.reporting.service.application.command.common.FindByIdCommand;
import com.sample.system.reporting.service.application.command.common.FindByReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.reporttemplate.CreateReportTemplateCommand;
import com.sample.system.reporting.service.application.command.reporttemplate.UpdateReportTemplateCommand;
import com.sample.system.reporting.service.application.mapper.ReportTemplateMapper;
import com.sample.system.reporting.service.application.ports.input.ReportTemplateService;
import com.sample.system.reporting.service.application.response.common.MutationResponse;
import com.sample.system.reporting.service.application.response.reporttemplate.ReportTemplateResponse;
import com.sample.system.reporting.service.application.util.JsonStructureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportTemplateCommandHandler {
    private final ReportTemplateService service;
    private final ReportTemplateMapper mapper;
    private final JsonStructureValidator jsonValidator;

    public MutationResponse create(CreateReportTemplateCommand command) {
        jsonValidator.requireArray(command.getColumnsJson());
        var saved = service.create(mapper.from(command));
        return new MutationResponse(saved.getId().getValue(), "Report template created successfully");
    }
    public MutationResponse update(UpdateReportTemplateCommand command) {
        jsonValidator.requireArray(command.getColumnsJson());
        var saved = service.update(mapper.from(command));
        return new MutationResponse(saved.getId().getValue(), "Report template updated successfully");
    }
    public ReportTemplateResponse find(FindByIdCommand command) {
        return mapper.toResponse(service.findById(command.getId()));
    }
    public List<ReportTemplateResponse> search(FindByReportDefinitionCommand command) {
        return service.findByReportDefinitionId(command.getReportDefinitionId()).stream()
                .map(mapper::toResponse).toList();
    }
    public MutationResponse delete(FindByIdCommand command) {
        service.delete(command.getId());
        return new MutationResponse(command.getId(), "Report template deleted successfully");
    }
}
