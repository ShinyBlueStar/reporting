package com.sample.system.reporting.service.application.rest.reporttemplate;

import com.sample.system.platform.commons.contracts.util.StandardResponse;
import com.sample.system.reporting.service.application.command.common.FindByIdCommand;
import com.sample.system.reporting.service.application.command.common.FindByReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.reporttemplate.CreateReportTemplateCommand;
import com.sample.system.reporting.service.application.command.reporttemplate.UpdateReportTemplateCommand;
import com.sample.system.reporting.service.application.handler.reporttemplate.ReportTemplateCommandHandler;
import com.sample.system.reporting.service.application.response.common.MutationResponse;
import com.sample.system.reporting.service.application.response.reporttemplate.ReportTemplateResponse;
import com.sample.system.reporting.service.application.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Report Templates")
@RequestMapping("/api/v1/report-template")
public class ReportTemplateController {
    private final ReportTemplateCommandHandler handler;

    @PostMapping
    public ResponseEntity<StandardResponse<MutationResponse>> create(
            @RequestBody @Valid CreateReportTemplateCommand command) {
        return ResponseBuilder.created(handler.create(command), "Report template created successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<StandardResponse<MutationResponse>> update(
            @RequestBody @Valid UpdateReportTemplateCommand command) {
        return ResponseBuilder.success(handler.update(command), "Report template updated successfully");
    }

    @PostMapping("/find")
    public ResponseEntity<StandardResponse<ReportTemplateResponse>> find(
            @RequestBody @Valid FindByIdCommand command) {
        return ResponseBuilder.success(handler.find(command), "Report template retrieved successfully");
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse<List<ReportTemplateResponse>>> search(
            @RequestBody @Valid FindByReportDefinitionCommand command) {
        return ResponseBuilder.success(handler.search(command), "Report templates retrieved successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<StandardResponse<MutationResponse>> delete(
            @RequestBody @Valid FindByIdCommand command) {
        return ResponseBuilder.success(handler.delete(command), "Report template deleted successfully");
    }
}
