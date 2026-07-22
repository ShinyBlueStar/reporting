package com.sample.system.reporting.service.application.handler.reportparameter;

import com.sample.system.reporting.service.application.command.common.FindByIdCommand;
import com.sample.system.reporting.service.application.command.common.FindByReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.reportparameter.CreateReportParameterCommand;
import com.sample.system.reporting.service.application.command.reportparameter.UpdateReportParameterCommand;
import com.sample.system.reporting.service.application.mapper.ReportParameterMapper;
import com.sample.system.reporting.service.application.ports.input.ReportParameterService;
import com.sample.system.reporting.service.application.response.common.MutationResponse;
import com.sample.system.reporting.service.application.response.reportparameter.ReportParameterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportParameterCommandHandler {
    private final ReportParameterService service;
    private final ReportParameterMapper mapper;

    public MutationResponse create(CreateReportParameterCommand command) {
        var saved = service.create(mapper.from(command));
        return new MutationResponse(saved.getId().getValue(), "Report parameter created successfully");
    }
    public MutationResponse update(UpdateReportParameterCommand command) {
        var saved = service.update(mapper.from(command));
        return new MutationResponse(saved.getId().getValue(), "Report parameter updated successfully");
    }
    public ReportParameterResponse find(FindByIdCommand command) {
        return mapper.toResponse(service.findById(command.getId()));
    }
    public List<ReportParameterResponse> search(FindByReportDefinitionCommand command) {
        return service.findByReportDefinitionId(command.getReportDefinitionId()).stream()
                .map(mapper::toResponse).toList();
    }
    public MutationResponse delete(FindByIdCommand command) {
        service.delete(command.getId());
        return new MutationResponse(command.getId(), "Report parameter deleted successfully");
    }
}
