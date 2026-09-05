package com.sample.system.reporting.service.application.rest.reportexecution;

import com.sample.system.platform.commons.contracts.util.StandardResponse;
import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.application.command.reportexecution.FindReportExecutionByIdCommand;
import com.sample.system.reporting.service.application.handler.reportexecution.ReportExecutionDownloadCommandHandler;
import com.sample.system.reporting.service.application.handler.reportexecution.ReportExecutionExecuteCommandHandler;
import com.sample.system.reporting.service.application.handler.reportexecution.ReportExecutionFindByIdCommandHandler;
import com.sample.system.reporting.service.application.response.reportexecution.ExecuteReportResponse;
import com.sample.system.reporting.service.application.response.reportexecution.FindReportExecutionResponse;
import com.sample.system.reporting.service.application.response.reportexecution.ReportFileDownloadResponse;
import com.sample.system.reporting.service.application.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Report Execution", description = "اجرای گزارش با پارامترهای ورودی")
@RequiredArgsConstructor
@RequestMapping("/api/v1/report-execution")
public class ReportExecutionController {

    private final ReportExecutionExecuteCommandHandler reportExecutionExecuteCommandHandler;
    private final ReportExecutionFindByIdCommandHandler reportExecutionFindByIdCommandHandler;
    private final ReportExecutionDownloadCommandHandler reportExecutionDownloadCommandHandler;

    @PostMapping("/execute")
    public ResponseEntity<StandardResponse<ExecuteReportResponse>> executeReport(
            @RequestBody @Valid ExecuteReportCommand command,
            java.security.Principal principal) {
        if (principal != null) {
            // The authenticated identity always wins over any client supplied value.
            command.setRequestedBy(principal.getName());
        }
        ExecuteReportResponse response = reportExecutionExecuteCommandHandler.executeReport(command);
        String message = "FAILED".equals(response.getExecutionStatus())
                ? "Report execution failed"
                : "Report executed successfully";
        return ResponseBuilder.success(response, message);
    }

    @GetMapping("/{reportExecutionId}/download")
    public ResponseEntity<Resource> downloadReportFile(@PathVariable Long reportExecutionId) {
        ReportFileDownloadResponse file = reportExecutionDownloadCommandHandler.downloadReportFile(reportExecutionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getContent());
    }

    @PostMapping("/find")
    public ResponseEntity<StandardResponse<FindReportExecutionResponse>> findReportExecutionById(
            @RequestBody @Valid FindReportExecutionByIdCommand command) {
        FindReportExecutionResponse response =
                reportExecutionFindByIdCommandHandler.findReportExecutionById(command);
        return ResponseBuilder.success(response, "Report execution retrieved successfully");
    }
}
