# Complete Class List — reporting-service

Total: **224** types

## reporting-application

### presentation (7)

| # | Name | Kind | Stereotype | Package |
|---|------|------|------------|---------|
| 1 | `BaseResponse` | class | class | `com.sample.system.reporting.service.application.response` |
| 2 | `ErrorDetail` | class | class | `com.sample.system.reporting.service.application.response` |
| 3 | `ReportDefinitionController` | class | Controller | `com.sample.system.reporting.service.application.rest.reportdefinition` |
| 4 | `ReportExecutionController` | class | Controller | `com.sample.system.reporting.service.application.rest.reportexecution` |
| 5 | `ReportingGlobalExceptionHandler` | class | ExceptionHandler | `com.sample.system.reporting.service.application.exception.handler` |
| 6 | `ResponseBuilder` | class | Utility | `com.sample.system.reporting.service.application.util` |
| 7 | `TrackingIdUtil` | class | Utility | `com.sample.system.reporting.service.application.util` |

## reporting-container

### container (3)

| # | Name | Kind | Stereotype | Package |
|---|------|------|------------|---------|
| 1 | `ReportBatchJobConfiguration` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 2 | `ReportBatchProperties` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 3 | `ReportingServiceApplication` | class | Bootstrap | `com.sample.system.reporting.service` |

## reporting-domain

### container (129)

| # | Name | Kind | Stereotype | Package |
|---|------|------|------------|---------|
| 1 | `AbstractProjectionEventHandler` | class | class | `com.sample.system.reporting.service.application.event.handler` |
| 2 | `AccountProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 3 | `AccountReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 4 | `AccountReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 5 | `AccountReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 6 | `AccountReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 7 | `AggregateRoot` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 8 | `BaseEntity` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 9 | `BaseId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 10 | `CardProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 11 | `CardReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 12 | `CardReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 13 | `CardReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 14 | `CardReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 15 | `CreateReportDefinitionCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportdefinition` |
| 16 | `CreateReportDefinitionResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportdefinition` |
| 17 | `DeleteReportDefinitionCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportdefinition` |
| 18 | `DeleteReportDefinitionResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportdefinition` |
| 19 | `ErrorCode` | enum | enumeration | `com.sample.system.reporting.service.domain.exception` |
| 20 | `EventEnvelope` | record | record | `com.sample.system.reporting.service.application.event.model` |
| 21 | `EventPayloadReader` | class | Utility | `com.sample.system.reporting.service.application.utility` |
| 22 | `EventStatus` | enum | enumeration | `com.sample.system.reporting.service.domain.model.enums` |
| 23 | `ExecuteReportCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportexecution` |
| 24 | `ExecuteReportResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportexecution` |
| 25 | `FindReportDefinitionByIdCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportdefinition` |
| 26 | `FindReportDefinitionResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportdefinition` |
| 27 | `FindReportExecutionByIdCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportexecution` |
| 28 | `FindReportExecutionResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportexecution` |
| 29 | `InstallmentProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 30 | `InstallmentReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 31 | `InstallmentReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 32 | `InstallmentReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 33 | `InstallmentReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 34 | `IReportDefinitionRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 35 | `IReportEventRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 36 | `IReportExecutionDetailRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 37 | `IReportExecutionRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 38 | `IReportFileRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 39 | `IReportParameterRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 40 | `IReportProjectionRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 41 | `IReportTemplateRepository` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 42 | `LoanProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 43 | `LoanReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 44 | `LoanReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 45 | `LoanReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 46 | `LoanReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 47 | `PagedSearchCommand` | class | Command | `com.sample.system.reporting.service.application.command.common` |
| 48 | `PartyProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 49 | `PartyReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 50 | `PartyReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 51 | `PartyReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 52 | `PartyReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 53 | `ProductProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 54 | `ProductReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 55 | `ProductReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 56 | `ProductReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 57 | `ProductReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 58 | `ProjectionEventDispatcher` | interface | Port | `com.sample.system.reporting.service.application.event.dispacher` |
| 59 | `ProjectionEventDispatcherImpl` | class | class | `com.sample.system.reporting.service.application.event.impl` |
| 60 | `ProjectionEventHandler` | interface | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 61 | `ProjectionPayloadSerializer` | class | Utility | `com.sample.system.reporting.service.application.utility` |
| 62 | `ProjectionReport` | interface | interface | `com.sample.system.reporting.service.domain.model.entity` |
| 63 | `ReportBatchContext` | class | Model | `com.sample.system.reporting.service.application.reportbatch.model` |
| 64 | `ReportBatchContextFactory` | class | Model | `com.sample.system.reporting.service.application.reportbatch.service` |
| 65 | `ReportBatchFormatResolver` | class | Model | `com.sample.system.reporting.service.application.reportbatch.service` |
| 66 | `ReportBatchJobParameterKeys` | class | Model | `com.sample.system.reporting.service.application.reportbatch.model` |
| 67 | `ReportBatchLauncherPort` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 68 | `ReportBatchLaunchRequest` | class | Model | `com.sample.system.reporting.service.application.reportbatch.model` |
| 69 | `ReportBatchProcessor` | interface | Port | `com.sample.system.reporting.service.application.reportbatch.contract` |
| 70 | `ReportBatchReader` | interface | Port | `com.sample.system.reporting.service.application.reportbatch.contract` |
| 71 | `ReportBatchSharedStateKeys` | class | Model | `com.sample.system.reporting.service.application.reportbatch.model` |
| 72 | `ReportBatchWriter` | interface | Port | `com.sample.system.reporting.service.application.reportbatch.contract` |
| 73 | `ReportCategory` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 74 | `ReportCategoryId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 75 | `ReportDefinition` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 76 | `ReportDefinitionCreateCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportdefinition` |
| 77 | `ReportDefinitionDeleteCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportdefinition` |
| 78 | `ReportDefinitionFindByIdCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportdefinition` |
| 79 | `ReportDefinitionId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 80 | `ReportDefinitionMapper` | class | Mapper | `com.sample.system.reporting.service.application.mapper` |
| 81 | `ReportDefinitionSearchCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportdefinition` |
| 82 | `ReportDefinitionSearchCriteria` | class | class | `com.sample.system.reporting.service.domain.model` |
| 83 | `ReportDefinitionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 84 | `ReportDefinitionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 85 | `ReportDefinitionStatusUpdateCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportdefinition` |
| 86 | `ReportDefinitionUpdateCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportdefinition` |
| 87 | `ReportEvent` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 88 | `ReportEventId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 89 | `ReportExecution` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 90 | `ReportExecutionDetail` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 91 | `ReportExecutionDetailId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 92 | `ReportExecutionDownloadCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportexecution` |
| 93 | `ReportExecutionExecuteCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportexecution` |
| 94 | `ReportExecutionFindByIdCommandHandler` | class | UseCase | `com.sample.system.reporting.service.application.handler.reportexecution` |
| 95 | `ReportExecutionId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 96 | `ReportExecutionMapper` | class | Mapper | `com.sample.system.reporting.service.application.mapper` |
| 97 | `ReportExecutionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 98 | `ReportExecutionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 99 | `ReportExecutionStatus` | enum | enumeration | `com.sample.system.reporting.service.domain.model.enums` |
| 100 | `ReportExecutionUrlFactory` | class | class | `com.sample.system.reporting.service.application.reportexecution` |
| 101 | `ReportFile` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 102 | `ReportFileDownloadResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportexecution` |
| 103 | `ReportFileId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 104 | `ReportFileStoragePort` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 105 | `ReportFormat` | enum | enumeration | `com.sample.system.reporting.service.domain.model.enums` |
| 106 | `ReportingDomainException` | class | class | `com.sample.system.reporting.service.domain.exception` |
| 107 | `ReportingProjectionMapper` | class | Mapper | `com.sample.system.reporting.service.application.mapper` |
| 108 | `ReportingSyncService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 109 | `ReportingSyncServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 110 | `ReportParameter` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 111 | `ReportParameterBindingService` | class | class | `com.sample.system.reporting.service.application.reportexecution` |
| 112 | `ReportParameterId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 113 | `ReportQueryExecutorPort` | interface | Port | `com.sample.system.reporting.service.application.ports.output` |
| 114 | `ReportSchedule` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 115 | `ReportScheduleId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 116 | `ReportSqlGuard` | class | class | `com.sample.system.reporting.service.application.reportexecution` |
| 117 | `ReportTemplate` | class | Entity | `com.sample.system.reporting.service.domain.model.entity` |
| 118 | `ReportTemplateId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 119 | `ReportType` | enum | enumeration | `com.sample.system.reporting.service.domain.model.enums` |
| 120 | `SearchReportDefinitionCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportdefinition` |
| 121 | `SearchReportDefinitionResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportdefinition` |
| 122 | `TransactionProjectionEventHandler` | class | Handler | `com.sample.system.reporting.service.application.event.handler` |
| 123 | `TransactionReport` | class | Aggregate | `com.sample.system.reporting.service.domain.model.entity` |
| 124 | `TransactionReportId` | class | ValueObject | `com.sample.system.reporting.service.domain.model.valueObject` |
| 125 | `TransactionReportProjectionService` | interface | Port | `com.sample.system.reporting.service.application.ports.input` |
| 126 | `TransactionReportProjectionServiceImpl` | class | Service | `com.sample.system.reporting.service.application.ports.input.impl` |
| 127 | `UpdateReportDefinitionCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportdefinition` |
| 128 | `UpdateReportDefinitionResponse` | class | DTO | `com.sample.system.reporting.service.application.response.reportdefinition` |
| 129 | `UpdateReportDefinitionStatusCommand` | class | Command | `com.sample.system.reporting.service.application.command.reportdefinition` |

## reporting-infrastructure

### infrastructure (82)

| # | Name | Kind | Stereotype | Package |
|---|------|------|------------|---------|
| 1 | `AccountReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 2 | `AccountReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 3 | `AccountReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 4 | `Audit` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity` |
| 5 | `Auditable` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity` |
| 6 | `BatchReportLauncherAdapter` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 7 | `CardReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 8 | `CardReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 9 | `CardReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 10 | `CsvReportFormatHandler` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 11 | `DelegatingReportItemProcessor` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 12 | `DelegatingReportItemReader` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 13 | `DelegatingReportItemWriter` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 14 | `GenericReportBatchProcessor` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 15 | `GenericReportBatchWriter` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 16 | `InstallmentReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 17 | `InstallmentReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 18 | `InstallmentReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 19 | `IReportEventPersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 20 | `IReportProjectionPersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 21 | `JdbcReportBatchReader` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 22 | `JsonReportFormatHandler` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 23 | `LoanReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 24 | `LoanReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 25 | `LoanReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 26 | `LocalReportFileStorageAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 27 | `PartyReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 28 | `PartyReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 29 | `PartyReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 30 | `ProductReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 31 | `ProductReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 32 | `ProductReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 33 | `public` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 34 | `ReportBatchComponentRegistry` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 35 | `ReportBatchSharedStateRegistry` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 36 | `ReportBatchStepCompletionListener` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 37 | `ReportCategoryDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 38 | `ReportCategoryEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 39 | `ReportDefinitionDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 40 | `ReportDefinitionEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 41 | `ReportDefinitionPersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 42 | `ReportDefinitionRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 43 | `ReportDefinitionSpecification` | class | class | `com.sample.system.reporting.service.dataaccess.specification` |
| 44 | `ReportEventDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 45 | `ReportEventEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 46 | `ReportEventRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 47 | `ReportExecutionDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 48 | `ReportExecutionDetailDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 49 | `ReportExecutionDetailEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 50 | `ReportExecutionDetailPersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 51 | `ReportExecutionDetailRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 52 | `ReportExecutionEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 53 | `ReportExecutionPersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 54 | `ReportExecutionRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 55 | `ReportFileDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 56 | `ReportFileEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 57 | `ReportFilePersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 58 | `ReportFileRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 59 | `ReportFormatColumn` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 60 | `ReportFormatHandler` | interface | Port | `com.sample.system.reporting.service.reportformat` |
| 61 | `ReportFormatHandlerRegistry` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 62 | `ReportFormatRequest` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 63 | `ReportFormatResult` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 64 | `ReportFormatService` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 65 | `ReportJdbcValueConverter` | class | Configuration | `com.sample.system.reporting.service.batch` |
| 66 | `ReportParameterDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 67 | `ReportParameterEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 68 | `ReportParameterPersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 69 | `ReportParameterRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 70 | `ReportQueryExecutorAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 71 | `ReportScheduleDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 72 | `ReportScheduleEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 73 | `ReportScheduleExecutionJob` | class | class | `com.sample.system.reporting.service.dataaccess.scheduler` |
| 74 | `ReportScheduleRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 75 | `ReportTemplateColumnResolver` | class | Service | `com.sample.system.reporting.service.reportformat` |
| 76 | `ReportTemplateDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 77 | `ReportTemplateEntity` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.command` |
| 78 | `ReportTemplatePersistenceAdapter` | class | Adapter | `com.sample.system.reporting.service.dataaccess.adapter` |
| 79 | `ReportTemplateRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |
| 80 | `TransactionReportDataAccessMapper` | interface | interface | `com.sample.system.reporting.service.dataaccess.mapper` |
| 81 | `TransactionReportEntityQuery` | class | JpaEntity | `com.sample.system.reporting.service.dataaccess.entity.query` |
| 82 | `TransactionReportRepository` | interface | Repository | `com.sample.system.reporting.service.dataaccess.repository` |

## reporting-messaging

### messaging (3)

| # | Name | Kind | Stereotype | Package |
|---|------|------|------------|---------|
| 1 | `RabbitMqConfiguration` | class | Configuration | `com.sample.system.reporting.service.messaging.config` |
| 2 | `ReportingEventConsumer` | class | Consumer | `com.sample.system.reporting.service.messaging.consumer` |
| 3 | `ReportingEventProcessor` | class | Processor | `com.sample.system.reporting.service.messaging.processor` |
