package com.sample.system.reporting.service.application.rest.reportdefinition;

import com.sample.system.platform.commons.contracts.util.StandardResponse;
import com.sample.system.reporting.service.application.command.reportdefinition.*;
import com.sample.system.reporting.service.application.handler.reportdefinition.*;
import com.sample.system.reporting.service.application.response.reportdefinition.*;
import com.sample.system.reporting.service.application.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Report Definitions", description = "مدیریت تعریف گزارش‌ها")
@RequiredArgsConstructor
@RequestMapping("/api/v1/report-definition")
public class ReportDefinitionController {

    private final ReportDefinitionCreateCommandHandler reportDefinitionCreateCommandHandler;
    private final ReportDefinitionUpdateCommandHandler reportDefinitionUpdateCommandHandler;
    private final ReportDefinitionFindCommandHandler reportDefinitionFindCommandHandler;
    private final ReportDefinitionSearchCommandHandler reportDefinitionSearchCommandHandler;
    private final ReportDefinitionDeleteCommandHandler reportDefinitionDeleteCommandHandler;
    private final ReportDefinitionStatusUpdateCommandHandler reportDefinitionStatusUpdateCommandHandler;

    @PostMapping
    public ResponseEntity<StandardResponse<CreateReportDefinitionResponse>> createReportDefinition(
            @RequestBody @Valid CreateReportDefinitionCommand command) {
        CreateReportDefinitionResponse response =
                reportDefinitionCreateCommandHandler.createReportDefinition(command);
        return ResponseBuilder.created(response, "Report definition created successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<StandardResponse<UpdateReportDefinitionResponse>> updateReportDefinition(
            @RequestBody @Valid UpdateReportDefinitionCommand command) {
        UpdateReportDefinitionResponse response =
                reportDefinitionUpdateCommandHandler.updateReportDefinition(command);
        return ResponseBuilder.success(response, "Report definition updated successfully");
    }

    @PostMapping("/find")
    public ResponseEntity<StandardResponse<FindReportDefinitionResponse>> findReportDefinition(
            @RequestBody FindReportDefinitionCommand command) {
        FindReportDefinitionResponse response =
                reportDefinitionFindCommandHandler.findReportDefinition(command);
        return ResponseBuilder.success(response, "Report definition retrieved successfully");
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse<SearchReportDefinitionResponse<FindReportDefinitionResponse>>> searchReportDefinitions(
            @RequestBody SearchReportDefinitionCommand command) {
        SearchReportDefinitionResponse<FindReportDefinitionResponse> response =
                reportDefinitionSearchCommandHandler.searchReportDefinitions(command);
        return ResponseBuilder.success(response, "Report definitions retrieved successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<StandardResponse<DeleteReportDefinitionResponse>> deleteReportDefinition(
            @RequestBody @Valid DeleteReportDefinitionCommand command) {
        DeleteReportDefinitionResponse response =
                reportDefinitionDeleteCommandHandler.deleteReportDefinition(command);
        return ResponseBuilder.success(response, "Report definition deleted successfully");
    }

    @PostMapping("/status")
    public ResponseEntity<StandardResponse<UpdateReportDefinitionResponse>> updateReportDefinitionStatus(
            @RequestBody @Valid UpdateReportDefinitionStatusCommand command) {
        UpdateReportDefinitionResponse response =
                reportDefinitionStatusUpdateCommandHandler.updateStatus(command);
        return ResponseBuilder.success(response, "Report definition status updated successfully");
    }
}
