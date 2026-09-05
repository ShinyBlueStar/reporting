package com.sample.system.reporting.service.application.ports.inbound.impl;

import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.application.mapper.ReportExecutionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.ports.input.impl.ReportExecutionServiceImpl;
import com.sample.system.reporting.service.application.ports.output.IReportExecutionDetailRepository;
import com.sample.system.reporting.service.application.ports.output.IReportExecutionRepository;
import com.sample.system.reporting.service.application.ports.output.IReportFileRepository;
import com.sample.system.reporting.service.application.ports.output.IReportParameterRepository;
import com.sample.system.reporting.service.application.ports.output.ReportBatchLauncherPort;
import com.sample.system.reporting.service.application.ports.output.ReportFileStoragePort;
import com.sample.system.reporting.service.application.ports.output.ReportQueryExecutorPort;
import com.sample.system.reporting.service.application.response.reportexecution.ExecuteReportResponse;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.application.reportbatch.service.ReportBatchFormatResolver;
import com.sample.system.reporting.service.application.reportexecution.ReportExecutionUrlFactory;
import com.sample.system.reporting.service.application.reportexecution.ReportExecutionTracker;
import com.sample.system.reporting.service.application.reportexecution.ReportParameterBindingService;
import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.ports.output.IValidationRuleDefinitionRepository;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.engine.DefaultValidationEngine;
import com.sample.system.reporting.service.application.validation.engine.ValidationEngine;
import com.sample.system.reporting.service.application.validation.registry.ValidationRuleRegistry;
import com.sample.system.reporting.service.application.validation.rule.impl.AtLeastOneRequiredValidator;
import com.sample.system.reporting.service.application.validation.rule.impl.DateRangeValidator;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.entity.ReportExecutionDetail;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import com.sample.system.reporting.service.domain.model.valueObject.ReportDefinitionId;
import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportExecutionServiceImplTest {

    private final ReportDefinitionService reportDefinitionService = mock(ReportDefinitionService.class);
    private final IReportParameterRepository reportParameterRepository = mock(IReportParameterRepository.class);
    private final IReportExecutionRepository reportExecutionRepository = mock(IReportExecutionRepository.class);
    private final IReportExecutionDetailRepository reportExecutionDetailRepository = mock(IReportExecutionDetailRepository.class);
    private final ReportQueryExecutorPort reportQueryExecutorPort = mock(ReportQueryExecutorPort.class);
    private final ReportBatchLauncherPort reportBatchLauncherPort = mock(ReportBatchLauncherPort.class);
    private final IReportFileRepository reportFileRepository = mock(IReportFileRepository.class);
    private final ReportFileStoragePort reportFileStoragePort = mock(ReportFileStoragePort.class);
    private final ReportExecutionUrlFactory reportExecutionUrlFactory = new ReportExecutionUrlFactory("http://localhost:8027");
    private final IValidationRuleDefinitionRepository validationRuleDefinitionRepository =
            mock(IValidationRuleDefinitionRepository.class);
    private final ValidationEngine validationEngine = createValidationEngine();

    private final ReportExecutionTracker executionTracker =
            new ReportExecutionTracker(reportExecutionDetailRepository);

    private final ReportExecutionServiceImpl service = new ReportExecutionServiceImpl(
            reportDefinitionService,
            reportParameterRepository,
            reportExecutionRepository,
            reportQueryExecutorPort,
            reportBatchLauncherPort,
            new ReportParameterBindingService(new ObjectMapper().findAndRegisterModules()),
            new ReportSqlGuard(),
            new ReportBatchFormatResolver(),
            new ReportExecutionMapper(),
            reportFileRepository,
            reportFileStoragePort,
            reportExecutionUrlFactory,
            executionTracker,
            validationRuleDefinitionRepository,
            validationEngine);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "batchChunkSize", 1000);
        when(validationRuleDefinitionRepository.findByReportDefinitionId(anyLong())).thenReturn(List.of());
        when(reportExecutionRepository.save(any(ReportExecution.class))).thenAnswer(invocation -> {
            ReportExecution execution = invocation.getArgument(0);
            if (execution.getId() == null) {
                execution.setId(new ReportExecutionId(100L));
            }
            return execution;
        });
        when(reportExecutionDetailRepository.save(any(ReportExecutionDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void executesBatchAndReturnsDownloadUrlWithoutPreviewRows() {
        ReportDefinition definition = definition("Portfolio", 2L);
        when(reportDefinitionService.findReportDefinitionById(10L)).thenReturn(definition);
        when(reportParameterRepository.findByReportDefinitionId(10L)).thenReturn(List.of());
        when(reportBatchLauncherPort.launch(any())).thenReturn(batchState(5L, 50L));
        doAnswer(invocation -> {
            Consumer<Map<String, Object>> consumer = invocation.getArgument(4);
            consumer.accept(Map.of("cnt", 5L));
            return null;
        }).when(reportQueryExecutorPort).executeQuery(
                eq("SELECT COUNT(*) AS cnt FROM (select * from report) report_count_query"),
                eq(Map.of()),
                eq(30),
                eq(1),
                any());

        ExecuteReportResponse response = service.executeReport(command());

        assertEquals("COMPLETED", response.getExecutionStatus());
        assertEquals(5L, response.getTotalRecordCount());
        assertEquals(50L, response.getGeneratedFileId());
        assertEquals("JSON", response.getReportFormat());
        assertEquals("http://localhost:8027/api/v1/report-execution/100/download", response.getDownloadUrl());
        verify(reportBatchLauncherPort).launch(any());
        assertLastSavedExecutionStatus("COMPLETED");
    }

    @Test
    void usesDefaultRowLimitWhenDefinitionLimitIsMissing() {
        ReportDefinition definition = definition("Portfolio", null);
        when(reportDefinitionService.findReportDefinitionById(10L)).thenReturn(definition);
        when(reportParameterRepository.findByReportDefinitionId(10L)).thenReturn(List.of());
        when(reportBatchLauncherPort.launch(any())).thenReturn(batchState(1L, 51L));

        ExecuteReportResponse response = service.executeReport(command());

        assertEquals("COMPLETED", response.getExecutionStatus());
        assertEquals(1L, response.getTotalRecordCount());
        assertNotNull(response.getDownloadUrl());
        verify(reportBatchLauncherPort).launch(any());
        verify(reportQueryExecutorPort, never()).executeQuery(
                startsWith("SELECT COUNT(*)"), anyMap(), any(), anyInt(), any());
    }

    @Test
    void finalizesFailureAndReturnsFailedResponseWhenBatchFails() {
        ReportDefinition definition = definition("Portfolio", 10L);
        when(reportDefinitionService.findReportDefinitionById(10L)).thenReturn(definition);
        when(reportParameterRepository.findByReportDefinitionId(10L)).thenReturn(List.of());
        when(reportBatchLauncherPort.launch(any()))
                .thenThrow(new ReportingDomainException("16", "database unavailable"));

        ExecuteReportResponse response = service.executeReport(command());

        assertEquals("FAILED", response.getExecutionStatus());
        assertTrue(response.getErrorMessage().contains("database unavailable"));
        assertNull(response.getDownloadUrl());
        assertLastSavedExecutionStatus("FAILED");

        ArgumentCaptor<ReportExecutionDetail> detailCaptor = ArgumentCaptor.forClass(ReportExecutionDetail.class);
        verify(reportExecutionDetailRepository, atLeastOnce()).save(detailCaptor.capture());
        assertTrue(detailCaptor.getAllValues().stream()
                .anyMatch(detail -> "FAILED".equals(detail.getStepName())
                        && "FAILED".equals(detail.getStepStatus())
                        && detail.getMessage().contains("database unavailable")));
    }

    @Test
    void rejectsInactiveReportBeforeCreatingExecution() {
        ReportDefinition definition = definition("Portfolio", 10L);
        definition.setActive(false);
        when(reportDefinitionService.findReportDefinitionById(10L)).thenReturn(definition);

        ReportingDomainException exception = assertThrows(
                ReportingDomainException.class,
                () -> service.executeReport(command()));

        assertTrue(exception.getMessage().contains("REPORT_DEFINITION_INACTIVE"));
        verify(reportExecutionRepository, never()).save(any());
        verify(reportBatchLauncherPort, never()).launch(any());
    }

    @Test
    void rejectsLoanPortfolioReportWithoutAnySearchParameterBeforeCreatingExecution() {
        ReportDefinition definition = definition("LoanPortfolioReportService", 10L);
        when(reportDefinitionService.findReportDefinitionById(10L)).thenReturn(definition);
        when(reportParameterRepository.findByReportDefinitionId(10L)).thenReturn(List.of(
                parameter("fileNumber", "STRING"),
                parameter("nationalCode", "STRING"),
                parameter("unitCode", "STRING"),
                parameter("unitName", "STRING"),
                parameter("fromDate", "DATETIME"),
                parameter("toDate", "DATETIME")
        ));
        when(validationRuleDefinitionRepository.findByReportDefinitionId(10L)).thenReturn(loanPortfolioValidationRules());

        ReportingDomainException exception = assertThrows(
                ReportingDomainException.class,
                () -> service.executeReport(command()));

        assertTrue(exception.getMessage().contains("At least one search parameter is required"));
        verify(reportExecutionRepository, never()).save(any());
    }

    @Test
    void rejectsLoanPortfolioReportWhenFromDateIsAfterToDate() {
        ReportDefinition definition = definition("LoanPortfolioReportService", 10L);
        when(reportDefinitionService.findReportDefinitionById(10L)).thenReturn(definition);
        when(reportParameterRepository.findByReportDefinitionId(10L)).thenReturn(List.of(
                parameter("fromDate", "DATETIME"),
                parameter("toDate", "DATETIME")
        ));
        when(validationRuleDefinitionRepository.findByReportDefinitionId(10L)).thenReturn(loanPortfolioValidationRules());
        ExecuteReportCommand command = command();
        command.setParameters(Map.of(
                "fromDate", Instant.parse("2026-01-02T00:00:00Z"),
                "toDate", Instant.parse("2026-01-01T00:00:00Z")));

        ReportingDomainException exception = assertThrows(
                ReportingDomainException.class,
                () -> service.executeReport(command));

        assertTrue(exception.getMessage().contains("fromDate must not be greater than toDate"));
        verify(reportExecutionRepository, never()).save(any());
    }

    private ConcurrentHashMap<String, Object> batchState(long processedCount, Long generatedFileId) {
        ConcurrentHashMap<String, Object> sharedState = new ConcurrentHashMap<>();
        ReportExecution batchExecution = new ReportExecution();
        batchExecution.setId(new ReportExecutionId(100L));
        batchExecution.setGeneratedFileId(generatedFileId);
        sharedState.put(ReportBatchSharedStateKeys.REPORT_EXECUTION, batchExecution);
        sharedState.put(ReportBatchSharedStateKeys.PROCESSED_ROW_COUNT, processedCount);
        return sharedState;
    }

    private ExecuteReportCommand command() {
        ExecuteReportCommand command = new ExecuteReportCommand();
        command.setReportDefinitionId(10L);
        command.setRequestedBy("tester");
        command.setReportFormat("JSON");
        return command;
    }

    private ReportDefinition definition(String code, Long maxExportRows) {
        ReportDefinition definition = new ReportDefinition();
        definition.setId(new ReportDefinitionId(10L));
        definition.setReportCode(code);
        definition.setReportName(code);
        definition.setSqlQuery("select * from report");
        definition.setTimeoutSeconds(30);
        definition.setActive(true);
        definition.setMaxExportRows(maxExportRows);
        definition.setReportType("JSON");
        return definition;
    }

    private ReportParameter parameter(String name, String type) {
        ReportParameter parameter = new ReportParameter();
        parameter.setParameterName(name);
        parameter.setParameterType(type);
        parameter.setRequired(false);
        return parameter;
    }

    private void assertLastSavedExecutionStatus(String status) {
        ArgumentCaptor<ReportExecution> executionCaptor = ArgumentCaptor.forClass(ReportExecution.class);
        verify(reportExecutionRepository, atLeastOnce()).save(executionCaptor.capture());
        List<ReportExecution> savedExecutions = executionCaptor.getAllValues();
        assertEquals(status, savedExecutions.get(savedExecutions.size() - 1).getExecutionStatus());
    }

    private ValidationEngine createValidationEngine() {
        ValidationConfigReader configReader = new ValidationConfigReader(new ObjectMapper());
        return new DefaultValidationEngine(new ValidationRuleRegistry(List.of(
                new AtLeastOneRequiredValidator(configReader),
                new DateRangeValidator(configReader))));
    }

    private List<ValidationRuleDefinition> loanPortfolioValidationRules() {
        ValidationRuleDefinition atLeastOne = new ValidationRuleDefinition();
        atLeastOne.setValidationRuleType(ValidationRuleType.AT_LEAST_ONE_REQUIRED);
        atLeastOne.setExecutionOrder(1);
        atLeastOne.setEnabled(true);
        atLeastOne.setErrorCode("2");
        atLeastOne.setErrorMessage("At least one search parameter is required");
        atLeastOne.setConfiguration(
                "{\"fields\":[\"fileNumber\",\"nationalCode\",\"unitCode\",\"unitName\",\"fromDate\",\"toDate\"]}");

        ValidationRuleDefinition dateRange = new ValidationRuleDefinition();
        dateRange.setValidationRuleType(ValidationRuleType.DATE_RANGE);
        dateRange.setExecutionOrder(2);
        dateRange.setEnabled(true);
        dateRange.setErrorCode("2");
        dateRange.setErrorMessage("fromDate must not be greater than toDate");
        dateRange.setConfiguration("{\"fromField\":\"fromDate\",\"toField\":\"toDate\"}");

        return List.of(atLeastOne, dateRange);
    }
}
