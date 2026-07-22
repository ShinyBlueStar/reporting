package com.sample.system.reporting.service.application.rest.reportparameter;

import com.sample.system.platform.commons.contracts.util.StandardResponse;
import com.sample.system.reporting.service.application.command.common.FindByIdCommand;
import com.sample.system.reporting.service.application.command.common.FindByReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.reportparameter.CreateReportParameterCommand;
import com.sample.system.reporting.service.application.command.reportparameter.UpdateReportParameterCommand;
import com.sample.system.reporting.service.application.handler.reportparameter.ReportParameterCommandHandler;
import com.sample.system.reporting.service.application.response.common.MutationResponse;
import com.sample.system.reporting.service.application.response.reportparameter.ReportParameterResponse;
import com.sample.system.reporting.service.application.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Report Parameters")
@RequestMapping("/api/v1/report-parameter")
public class ReportParameterController {
    private final ReportParameterCommandHandler handler;

    @PostMapping
    public ResponseEntity<StandardResponse<MutationResponse>> create(
            @RequestBody @Valid CreateReportParameterCommand command) {
        return ResponseBuilder.created(handler.create(command), "Report parameter created successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<StandardResponse<MutationResponse>> update(
            @RequestBody @Valid UpdateReportParameterCommand command) {
        return ResponseBuilder.success(handler.update(command), "Report parameter updated successfully");
    }

    @PostMapping("/find")
    public ResponseEntity<StandardResponse<ReportParameterResponse>> find(
            @RequestBody @Valid FindByIdCommand command) {
        return ResponseBuilder.success(handler.find(command), "Report parameter retrieved successfully");
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse<List<ReportParameterResponse>>> search(
            @RequestBody @Valid FindByReportDefinitionCommand command) {
        return ResponseBuilder.success(handler.search(command), "Report parameters retrieved successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<StandardResponse<MutationResponse>> delete(
            @RequestBody @Valid FindByIdCommand command) {
        return ResponseBuilder.success(handler.delete(command), "Report parameter deleted successfully");
    }
}
