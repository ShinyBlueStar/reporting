# PROJECT ARCHITECTURE

## 1. Project Overview

The Reporting Service is a Spring Boot service for defining SQL-backed reports, executing them manually or by schedule, consuming integration events into reporting projection tables, and producing downloadable report files.

Architecture style:

- Multi-module Maven application with a layered/hexagonal style.
- REST API module delegates to application command handlers.
- Application-service module owns use cases, ports, DTOs, batch context contracts, validation, mapping, and event dispatch.
- Infrastructure module implements persistence, JDBC execution, Spring Batch components, local file storage, format handlers, scheduling, and JPA repositories.
- Messaging module owns RabbitMQ topology and consumers.
- Container module starts the Spring Boot application and declares the Batch job.

Main technologies:

- Java 21
- Spring Boot 4.0.1
- Spring Web MVC
- Spring Data JPA
- Spring Batch
- Spring AMQP / RabbitMQ
- Spring Scheduling
- Spring Validation
- Spring Transaction
- Spring Cloud Eureka client
- Spring Actuator
- Flyway
- Oracle JDBC
- MapStruct
- Lombok
- Jackson
- Local filesystem storage

Spring modules used:

- `spring-boot-starter-web`: REST controllers and exception handling.
- `spring-boot-starter-data-jpa`: JPA entities, repositories, specifications.
- `spring-boot-starter-batch`: chunk-oriented report execution.
- `spring-boot-starter-amqp`: RabbitMQ listeners, exchanges, queues, bindings.
- `spring-boot-starter-validation`: request validation.
- `spring-tx`: transactional use cases.
- `spring-boot-starter-actuator`: operations endpoints.
- `spring-cloud-starter-netflix-eureka-client`: service discovery.

Spring Batch usage:

- Batch auto-start is disabled by `spring.batch.job.enabled=false`.
- Jobs are launched programmatically by `BatchReportLauncherAdapter`.
- One job exists: `reportExecutionBatchJob`.
- One chunk step exists: `reportExecutionBatchStep`.
- The step delegates reading, processing, and writing to registry-selected report components.
- Current concrete reader/processor/writer are generic SQL report components.

RabbitMQ usage:

- `RabbitMqConfiguration` enables Rabbit, declares one topic exchange, one direct dead-letter exchange, five durable queues, five bindings, and a listener container factory with manual acknowledgement.
- `ReportingEventConsumer` listens to party, card, loan, accounting, and product queues.
- Every listener delegates the message body to `ReportingEventProcessor`, then `basicAck`s on success and `basicNack`s without requeue on failure.

Scheduling:

- `ReportingServiceApplication` enables scheduling.
- `ReportScheduleExecutionJob` runs on `reporting.scheduler.fixed-delay-ms` with a default fixed delay of 60000 ms.
- It scans active schedules, evaluates cron next execution time, creates `ExecuteReportCommand`, and calls `ReportExecutionService.executeReport`.

JasperReports usage:

- No JasperReports dependency, import, or formatter implementation is present.

JDBC usage:

- `JdbcReportBatchReader` uses `DataSource`, manual `Connection`, `PreparedStatement`, `ResultSet`, named-parameter parsing, fetch size, and timeout.
- `ReportQueryExecutorAdapter` uses `NamedParameterJdbcTemplate.getJdbcTemplate().query(...)` for count/preview-style execution.
- Dynamic SQL comes from persisted `ReportDefinition.sqlQuery`.

JPA usage:

- Command entities persist report definitions, parameters, executions, files, templates, schedules, categories, and execution details.
- Query entities persist projection/read-model data from integration events.
- Spring Data repositories provide CRUD and derived queries.
- MapStruct mappers convert between JPA entities and domain entities.

## 2. Package Structure

```text
com.sample.system.reporting.service
├── ReportingServiceApplication
├── application
│   ├── command
│   │   ├── common
│   │   ├── reportdefinition
│   │   └── reportexecution
│   ├── event
│   │   ├── dispacher
│   │   │   └── impl
│   │   ├── handler
│   │   └── model
│   ├── exception.handler
│   ├── handler
│   │   ├── reportdefinition
│   │   └── reportexecution
│   ├── mapper
│   ├── ports
│   │   ├── input
│   │   │   └── impl
│   │   └── output
│   ├── reportbatch
│   │   ├── contract
│   │   ├── model
│   │   └── service
│   ├── reportexecution
│   ├── response
│   │   ├── reportdefinition
│   │   └── reportexecution
│   ├── rest
│   │   ├── reportdefinition
│   │   └── reportexecution
│   └── util
├── batch
├── dataaccess
│   ├── adapter
│   ├── entity
│   │   ├── command
│   │   └── query
│   ├── mapper
│   ├── repository
│   ├── scheduler
│   └── specification
├── domain
│   ├── exception
│   └── model
│       ├── entity
│       ├── enums
│       └── valueObject
├── messaging
│   ├── config
│   ├── consumer
│   └── processor
└── reportformat
```

## 3. Class Inventory

Scope: production Java classes under `src/main/java`; the project contains about 224 production Java types across 6 Maven modules.

Common creation/calling rules:

- `@RestController`, `@Service`, `@Component`, `@Configuration`, `@Repository`, `@Mapper`, and Spring Data repository interfaces are created by Spring.
- Plain DTOs, commands, responses, domain entities, value objects, and delegate wrappers are created by application code, Lombok constructors/builders, mappers, or Spring Batch configuration methods.
- JPA entities are created by JPA/Hibernate during persistence operations and by adapters/mappers when saving.
- MapStruct mapper implementations are generated at build time and registered as Spring beans.

| Class | Package | Responsibility | Superclass / Interfaces | Spring annotations | Injected dependencies | Public methods | Used / Created by / Called by |
|---|---|---|---|---|---|---|---|
| `ReportingServiceApplication` | root | Boot entry point and scheduling enabler. | none | `@SpringBootApplication`, `@EnableScheduling` | none | `main` | Used; JVM creates, Spring Boot starts scanning. |
| `ReportBatchJobConfiguration` | `batch` | Declares report Batch job, step, step-scoped context, reader, processor, writer beans. | none | `@Configuration`, `@EnableBatchProcessing`, `@Bean`, `@StepScope` | method parameters | bean factory methods | Used; Spring creates, Batch infrastructure calls. |
| `ReportBatchProperties` | `batch` | Holds configured batch chunk size. | none | `@Component`, `@ConfigurationProperties(prefix="reporting.batch")` | none | getters/setters | Used by `ReportBatchJobConfiguration`. |
| `ReportExecutionController` | `application.rest.reportexecution` | REST API for execute, find, and download report execution. | none | `@RestController`, `@RequestMapping` | execute/find/download handlers | `executeReport`, `downloadReportFile`, `findReportExecutionById` | Used; HTTP client calls, Spring creates. |
| `ReportDefinitionController` | `application.rest.reportdefinition` | REST API for report definition CRUD/search/status. | none | `@RestController`, `@RequestMapping` | definition command handlers | create/update/find/search/delete/status methods | Used; HTTP client calls, Spring creates. |
| `ReportingGlobalExceptionHandler` | `application.exception.handler` | Maps domain and validation exceptions to API responses. | `ResponseEntityExceptionHandler` | `@RestControllerAdvice`, exception handlers | none | exception handler methods | Used by Spring MVC exception pipeline. |
| `ResponseBuilder` | `application.util` | Builds standardized success/created HTTP responses. | none | none | none | `success`, `created` | Used by controllers. |
| `TrackingIdUtil` | `application.util` | Thread-local tracking id helper used while building platform responses. | none | none | none | generate/set/get/clear tracking id | Used by `ResponseBuilder`. |
| `BaseResponse` | `application.response` | Generic local response wrapper. | none | none | none | constructors/getters/setters | Used by exception handler. |
| `ErrorDetail` | `application.response` | Local error detail response body. | none | none | none | Lombok-generated accessors | Used by exception handler. |
| `ReportExecutionExecuteCommandHandler` | `application.handler.reportexecution` | Thin command handler for execution. | none | `@Component` | `ReportExecutionService` | `executeReport` | Used by `ReportExecutionController`. |
| `ReportExecutionFindByIdCommandHandler` | `application.handler.reportexecution` | Thin command handler for execution lookup. | none | `@Component` | `ReportExecutionService`, `ReportExecutionMapper` | `findReportExecutionById` | Used by `ReportExecutionController`. |
| `ReportExecutionDownloadCommandHandler` | `application.handler.reportexecution` | Thin command handler for report file download. | none | `@Component` | `ReportExecutionService` | `downloadReportFile` | Used by `ReportExecutionController`. |
| `ReportDefinitionCreateCommandHandler` | `application.handler.reportdefinition` | Validates and creates report definitions. | none | `@Component` | `ReportDefinitionService`, `ReportDefinitionMapper`, `ReportSqlGuard` | `createReportDefinition` | Used by `ReportDefinitionController`. |
| `ReportDefinitionUpdateCommandHandler` | `application.handler.reportdefinition` | Validates and updates report definitions. | none | `@Component` | `ReportDefinitionService`, `ReportDefinitionMapper`, `ReportSqlGuard` | `updateReportDefinition` | Used by `ReportDefinitionController`. |
| `ReportDefinitionFindByIdCommandHandler` | `application.handler.reportdefinition` | Looks up one report definition. | none | `@Component` | `ReportDefinitionService`, `ReportDefinitionMapper` | `findReportDefinitionById` | Used by `ReportDefinitionController`. |
| `ReportDefinitionSearchCommandHandler` | `application.handler.reportdefinition` | Searches report definitions. | none | `@Component` | `ReportDefinitionService`, `ReportDefinitionMapper` | `searchReportDefinitions` | Used by `ReportDefinitionController`. |
| `ReportDefinitionDeleteCommandHandler` | `application.handler.reportdefinition` | Deletes report definitions. | none | `@Component` | `ReportDefinitionService` | `deleteReportDefinition` | Used by `ReportDefinitionController`. |
| `ReportDefinitionStatusUpdateCommandHandler` | `application.handler.reportdefinition` | Updates active/status flag. | none | `@Component` | `ReportDefinitionService`, `ReportDefinitionMapper` | `updateStatus` | Used by `ReportDefinitionController`. |
| `ReportExecutionServiceImpl` | `application.ports.input.impl` | Main report execution use case: validate, persist execution, launch Batch, finalize status, and download file. | implements `ReportExecutionService` | `@Service`, `@Transactional` on execute | definition service, repositories, query executor, batch launcher, binding service, SQL guard, format resolver, mapper, file storage, URL factory | `executeReport`, `findReportExecutionById`, `downloadReportFile` | Used by execution handlers and scheduler. |
| `ReportDefinitionServiceImpl` | `application.ports.input.impl` | Application service for report definition CRUD/search. | implements `ReportDefinitionService` | `@Service` | `IReportDefinitionRepository` | definition CRUD/search methods | Used by definition handlers and batch context factory. |
| `ReportingSyncServiceImpl` | `application.ports.input.impl` | Idempotent event sync orchestration. | implements `ReportingSyncService` | `@Service` | `ProjectionEventDispatcher`, `IReportEventRepository` | `sync` | Used by `ReportingEventProcessor`. |
| `AccountReportProjectionServiceImpl` | `application.ports.input.impl` | Saves account projection reports. | implements `AccountReportProjectionService` | `@Service` | projection repository | projection save method | Used by account event handler. |
| `CardReportProjectionServiceImpl` | same | Saves card projection reports. | implements `CardReportProjectionService` | `@Service` | projection repository | projection save method | Used by card event handler. |
| `InstallmentReportProjectionServiceImpl` | same | Saves installment projection reports. | implements `InstallmentReportProjectionService` | `@Service` | projection repository | projection save method | Used by installment event handler. |
| `LoanReportProjectionServiceImpl` | same | Saves loan projection reports. | implements `LoanReportProjectionService` | `@Service` | projection repository | projection save method | Used by loan event handler. |
| `PartyReportProjectionServiceImpl` | same | Saves party projection reports. | implements `PartyReportProjectionService` | `@Service` | projection repository | projection save method | Used by party event handler. |
| `ProductReportProjectionServiceImpl` | same | Saves product projection reports. | implements `ProductReportProjectionService` | `@Service` | projection repository | projection save method | Used by product event handler. |
| `TransactionReportProjectionServiceImpl` | same | Saves transaction/accounting projection reports. | implements `TransactionReportProjectionService` | `@Service` | projection repository | projection save method | Used by transaction event handler. |
| `ReportExecutionService` | `application.ports.input` | Input port for report execution. | interface | none | none | execute/find/download declarations | Implemented by `ReportExecutionServiceImpl`; used by handlers/scheduler. |
| `ReportDefinitionService` | same | Input port for report definitions. | interface | none | none | CRUD/search declarations | Implemented by `ReportDefinitionServiceImpl`; used by handlers/context factory. |
| `ReportingSyncService` | same | Input port for event projection sync. | interface | none | none | `sync` | Implemented by `ReportingSyncServiceImpl`; used by messaging processor. |
| Projection input ports | same | `AccountReportProjectionService`, `CardReportProjectionService`, `InstallmentReportProjectionService`, `LoanReportProjectionService`, `PartyReportProjectionService`, `ProductReportProjectionService`, `TransactionReportProjectionService` define projection save use cases. | interfaces | none | none | save/upsert methods | Used by event handlers; one implementation each. |
| Output repository ports | `application.ports.output` | `IReportDefinitionRepository`, `IReportExecutionRepository`, `IReportExecutionDetailRepository`, `IReportFileRepository`, `IReportParameterRepository`, `IReportTemplateRepository`, `IReportProjectionRepository`, `IReportEventRepository` abstract persistence. | interfaces | none | none | repository methods | Implemented by infrastructure adapters; used by services/readers. |
| Other output ports | `application.ports.output` | `ReportBatchLauncherPort`, `ReportFileStoragePort`, `ReportQueryExecutorPort` abstract batch launch, file read, and SQL execution. | interfaces | none | none | launch/read/execute methods | Implemented by infrastructure adapters. |
| Command DTOs | `application.command.*` | `CreateReportDefinitionCommand`, `UpdateReportDefinitionCommand`, `UpdateReportDefinitionStatusCommand`, `SearchReportDefinitionCommand`, `FindReportDefinitionByIdCommand`, `DeleteReportDefinitionCommand`, `ExecuteReportCommand`, `FindReportExecutionByIdCommand`, `PagedSearchCommand` carry API input. | plain classes | validation annotations on fields where present | none | Lombok/manual accessors | Created by Jackson MVC; called by controllers/handlers. |
| Response DTOs | `application.response.*` | `CreateReportDefinitionResponse`, `UpdateReportDefinitionResponse`, `DeleteReportDefinitionResponse`, `FindReportDefinitionResponse`, `SearchReportDefinitionResponse`, `ExecuteReportResponse`, `FindReportExecutionResponse`, `ReportFileDownloadResponse` carry use-case output. | plain classes | none | none | Lombok-generated accessors | Created by mappers/services; returned by controllers. |
| `ReportDefinitionMapper` | `application.mapper` | Maps definition commands/domain/responses and applies updates. | none | `@Component` | none | create/update/toFind/toSearch methods | Used by definition handlers. |
| `ReportExecutionMapper` | `application.mapper` | Maps execution domain to execute/find responses. | none | `@Component` | none | `toExecuteResponse`, `toFailedExecuteResponse`, `toFindResponse` | Used by execution service and handlers. |
| `ReportingProjectionMapper` | `application.mapper` | Maps event payload/envelopes to projection domain objects. | none | `@Component` | `ObjectMapper` or mapping helpers | projection mapping methods | Used by projection event handlers. |
| `ProjectionEventDispatcher` / `ProjectionEventDispatcherImpl` | `application.event.dispacher` | Dispatches event envelopes to first supporting projection handler. | interface / implementation | `@Service` on impl | `List<ProjectionEventHandler>` | `dispatch` | Used by `ReportingSyncServiceImpl`; created by Spring. |
| `ProjectionEventHandler` | `application.event.handler` | Contract for event handlers. | interface | none | none | `supports`, `handle` | Implemented by concrete handlers; used by dispatcher. |
| `AbstractProjectionEventHandler` | same | Shared aggregate-type support check. | abstract, implements `ProjectionEventHandler` | none | none | `supports` | Superclass of concrete projection handlers. |
| Concrete projection handlers | same | `AccountProjectionEventHandler`, `CardProjectionEventHandler`, `InstallmentProjectionEventHandler`, `LoanProjectionEventHandler`, `PartyProjectionEventHandler`, `ProductProjectionEventHandler`, `TransactionProjectionEventHandler` route supported event payloads to projection services. | extend `AbstractProjectionEventHandler` | `@Component`/`@Service` where declared | matching projection service, mapper | `handle` | Used by dispatcher. |
| `EventEnvelope` | `application.event.model` | Immutable event wrapper parsed from RabbitMQ JSON. | record/class | none | none | accessors | Created by Jackson; used by sync/dispatch. |
| `ReportParameterBindingService` | `application.reportexecution` | Resolves, validates, converts, and serializes report parameters. | none | `@Component` | `ObjectMapper` | resolve/validate/toJson methods | Used by `ReportExecutionServiceImpl`. |
| `ReportSqlGuard` | same | Validates that report SQL is selectable and blocks dangerous SQL. | none | `@Component` | none | `ensureSelectableQuery` | Used by definition handlers, execution service, JDBC reader. |
| `ReportExecutionUrlFactory` | same | Builds public download URLs. | none | `@Component` | `publicBaseUrl` value | `downloadUrl` | Used by `ReportExecutionServiceImpl`. |
| Batch contracts/models | `application.reportbatch.*` | `ReportBatchReader`, `ReportBatchProcessor`, `ReportBatchWriter`, `ReportBatchContext`, `ReportBatchLaunchRequest`, `ReportBatchJobParameterKeys`, `ReportBatchSharedStateKeys` define the application-side batch contract and shared-state keys. | interfaces/plain classes | none | none | contract/model accessors | Used by Batch configuration and infrastructure components. |
| `BatchReportLauncherAdapter` | `batch` | Converts launch request into `JobParameters`, runs Batch job, returns released shared state. | implements `ReportBatchLauncherPort` | `@Component` | `JobLauncher`, `Job`, `ObjectMapper`, shared-state registry | `launch` | Used by `ReportExecutionServiceImpl`. |
| `ReportBatchComponentRegistry` | `batch` | Selects reader/processor/writer supporting a report. | none | `@Component` | lists of batch component beans | `readerFor`, `processorFor`, `writerFor` | Used by step-scoped bean factories. |
| `ReportBatchSharedStateRegistry` | `batch` | In-memory registry from execution id to shared state map. | none | `@Component` | none | `bind`, `get`, `release` | Used by context factory, launcher, step listener. |
| `JdbcReportBatchReader` | `batch` | Streams report SQL rows from JDBC cursor into maps; stores preview/template/columns in shared state. | implements `ReportBatchReader<Map<String,Object>>` | `@Component` | `DataSource`, template repository, SQL guard | `supports`, `open`, `read`, `close` | Used by registry/delegating reader. |
| `GenericReportBatchProcessor` | `batch` | Counts rows, enforces export limit, accumulates export rows in memory. | implements `ReportBatchProcessor<Map<String,Object>,Map<String,Object>>` | `@Component` | none | `supports`, `process` | Used by registry/delegating processor. |
| `GenericReportBatchWriter` | `batch` | No-op writer; formatting is deferred to step listener. | implements `ReportBatchWriter<Map<String,Object>>` | `@Component` | none | `supports`, `write` | Used by registry/delegating writer. |
| `DelegatingReportItemReader` | `batch` | Spring Batch `ItemStreamReader` adapter around application reader. | implements `ItemStreamReader<Object>` | none | constructor delegate/context | `open`, `read`, `close` | Created by `ReportBatchJobConfiguration`; called by Batch. |
| `DelegatingReportItemProcessor` | `batch` | Spring Batch `ItemProcessor` adapter. | implements `ItemProcessor<Object,Object>` | none | constructor delegate/context | `process` | Created by configuration; called by Batch. |
| `DelegatingReportItemWriter` | `batch` | Spring Batch `ItemWriter` adapter. | implements `ItemWriter<Object>` | none | constructor delegate/context | `write` | Created by configuration; called by Batch. |
| `ReportBatchStepCompletionListener` | `batch` | Formats accumulated rows, writes local file, persists file metadata, stores `FORMAT_RESULT`. | implements `StepExecutionListener` | `@Component` | registry, format service, file repository, storage properties | `afterStep` | Called by Batch after step. |
| `ReportJdbcValueConverter` | `batch` | Converts JDBC values into JSON/report-friendly values. | utility class | none | none | static convert method | Used by JDBC reader and query executor. |
| Report format classes | `reportformat` | `ReportFormatService`, `ReportFormatHandlerRegistry`, `ReportFormatHandler`, `ReportFormatRequest`, `ReportFormatResult`, `ReportFormatColumn`, `ReportTemplateColumnResolver`, `CsvReportFormatHandler`, `JsonReportFormatHandler` implement format selection and in-memory JSON/CSV byte generation. | service/component/interface/plain classes | `@Service`, `@Component` on service/registry/handlers/resolver | handler list, object mapper, column resolver | `format`, `findHandler`, `supports`, `resolveColumns`, accessors | Used by step listener. |
| Persistence adapters | `dataaccess.adapter` | `ReportDefinitionPersistenceAdapter`, `ReportExecutionPersistenceAdapter`, `ReportExecutionDetailPersistenceAdapter`, `ReportFilePersistenceAdapter`, `ReportParameterPersistenceAdapter`, `ReportTemplatePersistenceAdapter`, `IReportEventPersistenceAdapter`, `IReportProjectionPersistenceAdapter`, `ReportQueryExecutorAdapter`, `LocalReportFileStorageAdapter` implement output ports. | implement output ports | `@Component` | JPA repositories, mappers, JDBC template, storage property | save/find/search/execute/read methods | Used by services, batch reader, scheduler. |
| Spring Data repositories | `dataaccess.repository` | `ReportDefinitionRepository`, `ReportExecutionRepository`, `ReportExecutionDetailRepository`, `ReportFileRepository`, `ReportParameterRepository`, `ReportTemplateRepository`, `ReportScheduleRepository`, `ReportEventRepository`, and seven projection repositories provide JPA access. | extend `JpaRepository`; definition also `JpaSpecificationExecutor` | Spring Data repository beans | none | derived query methods | Used by adapters, reader, scheduler. |
| JPA command entities | `dataaccess.entity.command` | `ReportDefinitionEntity`, `ReportExecutionEntity`, `ReportExecutionDetailEntity`, `ReportFileEntity`, `ReportParameterEntity`, `ReportTemplateEntity`, `ReportScheduleEntity`, `ReportCategoryEntity` persist report configuration/execution metadata. `ReportCategoryEntity` is scaffold-only in the current runtime. | extend `Auditable` or plain entity base where declared | `@Entity`, table/id annotations | none | Lombok-generated accessors | Used by repositories/adapters/JPA, except category scaffold. |
| JPA query entities | `dataaccess.entity.query` | `BaseReportEntityQuery`, `AccountReportEntityQuery`, `CardReportEntityQuery`, `InstallmentReportEntityQuery`, `LoanReportEntityQuery`, `PartyReportEntityQuery`, `ProductReportEntityQuery`, `TransactionReportEntityQuery`, `ReportEventEntity` persist projection/read-model data and processed events. | projection entities extend `BaseReportEntityQuery`; event extends `Audit` | `@Entity` | none | Lombok-generated accessors | Used by projection repositories/adapters/JPA. |
| Audit support | `dataaccess.entity` | `Audit`, `Auditable` centralize audit fields. | mapped superclass/base class | JPA audit annotations where declared | none | accessors | Used by JPA entities. |
| MapStruct mappers | `dataaccess.mapper` | `ReportDefinitionDataAccessMapper`, `ReportExecutionDataAccessMapper`, `ReportExecutionDetailDataAccessMapper`, `ReportFileDataAccessMapper`, `ReportParameterDataAccessMapper`, `ReportTemplateDataAccessMapper`, `ReportCategoryDataAccessMapper`, `ReportScheduleDataAccessMapper`, `ReportEventDataAccessMapper`, and seven projection mappers convert JPA/domain types. Category, schedule, and event mappers are currently unused. | interfaces | `@Mapper(componentModel="spring")` | generated by MapStruct | mapper methods | Used by persistence adapters, except the unused scaffold mappers. |
| `ReportDefinitionSpecification` | `dataaccess.specification` | Builds JPA `Specification` for definition search. | utility class | none | none | `from` | Used by `ReportDefinitionPersistenceAdapter`. |
| `ReportScheduleExecutionJob` | `dataaccess.scheduler` | Periodically executes due report schedules. | none | `@Component`, `@Scheduled` | schedule repository, definition repository, execution service | `executeDueSchedules` | Called by Spring scheduler. |
| RabbitMQ classes | `messaging.*` | `RabbitMqConfiguration` declares topology/container factory; `ReportingEventConsumer` consumes queues; `ReportingEventProcessor` parses messages and invokes sync service. | config/component classes | `@Configuration`, `@EnableRabbit`, `@Component`, `@RabbitListener`, `@Bean` | connection factory, processor, object mapper, sync service | queue/listener/process methods | Used by Spring AMQP runtime. |
| Domain entities | `domain.model.entity` | `AggregateRoot`, `BaseEntity`, `ReportDefinition`, `ReportParameter`, `ReportExecution`, `ReportExecutionDetail`, `ReportFile`, `ReportTemplate`, `ReportSchedule`, `ReportCategory`, `ReportEvent`, `ProjectionReport`, `AccountReport`, `CardReport`, `InstallmentReport`, `LoanReport`, `PartyReport`, `ProductReport`, `TransactionReport` model reporting data without Spring dependencies. `ReportCategory`, domain `ReportSchedule`, and domain `ReportEvent` are scaffold-only in current runtime. | aggregates/entities; projection reports implement `ProjectionReport` | none | none | accessors/domain methods | Created by mappers/services; scaffold-only noted above. |
| Value objects | `domain.model.valueObject` | `BaseId`, `ReportDefinitionId`, `ReportExecutionId`, `ReportExecutionDetailId`, `ReportFileId`, `ReportParameterId`, `ReportTemplateId`, `ReportScheduleId`, `ReportCategoryId`, `ReportEventId`, `AccountReportId`, `CardReportId`, `InstallmentReportId`, `LoanReportId`, `PartyReportId`, `ProductReportId`, `TransactionReportId` wrap identity values. | most extend `BaseId<Long>` | none | none | constructors/accessors | Created by mappers/domain entities. |
| Domain enums/models/exceptions | `domain.*` | `ReportExecutionStatus`, `ReportFormat`, `ReportType`, `EventStatus`, `ReportDefinitionSearchCriteria`, `ErrorCode`, `ReportingDomainException` define status/format/search/error vocabulary. | enum/plain/exception | none | none | enum/static/accessor methods | Used throughout service. |

## 4. Dependency Graph

Primary runtime graph:

- `ReportExecutionController` uses `ReportExecutionExecuteCommandHandler`, `ReportExecutionFindByIdCommandHandler`, `ReportExecutionDownloadCommandHandler`; used by HTTP clients.
- Execution handlers use `ReportExecutionService`; used by controller.
- `ReportExecutionServiceImpl` uses `ReportDefinitionService`, `IReportParameterRepository`, `IReportExecutionRepository`, `IReportExecutionDetailRepository`, `ReportQueryExecutorPort`, `ReportBatchLauncherPort`, `ReportParameterBindingService`, `ReportSqlGuard`, `ReportBatchFormatResolver`, `ReportExecutionMapper`, `IReportFileRepository`, `ReportFileStoragePort`, `ReportExecutionUrlFactory`; used by handlers and scheduler.
- `BatchReportLauncherAdapter` uses `JobLauncher`, `reportExecutionBatchJob`, `ObjectMapper`, `ReportBatchSharedStateRegistry`; used by `ReportExecutionServiceImpl`.
- `ReportBatchJobConfiguration` uses `ReportBatchContextFactory`, `ReportBatchSharedStateRegistry`, `ReportBatchComponentRegistry`, step listener, Batch infrastructure; used by Spring.
- `ReportBatchContextFactory` uses `ReportDefinitionService`, `IReportExecutionRepository`, `ObjectMapper`; used by step-scoped context bean.
- `ReportBatchComponentRegistry` uses all `ReportBatchReader`, `ReportBatchProcessor`, `ReportBatchWriter` beans; used by step-scoped reader/processor/writer factories.
- `JdbcReportBatchReader` uses `DataSource`, `IReportTemplateRepository`, `ReportSqlGuard`; used by registry/delegating reader.
- `GenericReportBatchProcessor` uses shared state only; used by registry/delegating processor.
- `GenericReportBatchWriter` uses no dependency; used by registry/delegating writer.
- `ReportBatchStepCompletionListener` uses `ReportBatchSharedStateRegistry`, `ReportFormatService`, `ReportFileRepository`; used by Batch step.
- `ReportFormatService` uses `ReportFormatHandlerRegistry`; used by step listener.
- `ReportFormatHandlerRegistry` uses `List<ReportFormatHandler>`; used by format service.
- `CsvReportFormatHandler` uses `ReportTemplateColumnResolver`; used by registry.
- `JsonReportFormatHandler` uses `ObjectMapper`; used by registry.
- `LocalReportFileStorageAdapter` uses local path property; used by `ReportExecutionServiceImpl` download.

Definition management graph:

- `ReportDefinitionController` uses six definition handlers.
- Definition handlers use `ReportDefinitionService`, `ReportDefinitionMapper`, and sometimes `ReportSqlGuard`.
- `ReportDefinitionServiceImpl` uses `IReportDefinitionRepository`.
- `ReportDefinitionPersistenceAdapter` uses `ReportDefinitionRepository`, `ReportDefinitionDataAccessMapper`, `ReportDefinitionSpecification`.

Messaging graph:

- `ReportingEventConsumer` uses `ReportingEventProcessor`; used by Rabbit listener containers.
- `ReportingEventProcessor` uses `ObjectMapper`, `ReportingSyncService`; used by consumer.
- `ReportingSyncServiceImpl` uses `IReportEventRepository`, `ProjectionEventDispatcher`; used by processor.
- `ProjectionEventDispatcherImpl` uses `List<ProjectionEventHandler>`; used by sync service.
- Concrete projection handlers use projection services and `ReportingProjectionMapper`; used by dispatcher.
- Projection service implementations use `IReportProjectionRepository`; used by handlers.
- `IReportProjectionPersistenceAdapter` uses seven projection repositories and seven projection mappers; used by projection services.

Persistence graph:

- Application services depend on output ports.
- Output ports are implemented by infrastructure adapters.
- Adapters depend on Spring Data repositories and MapStruct mappers.
- Repositories depend on JPA entities.

## 5. Spring Batch Flow

Job:

- Name: `reportExecutionBatchJob`
- Defined by: `ReportBatchJobConfiguration.reportExecutionBatchJob`
- Starts with: `reportExecutionBatchStep`
- Launched by: `BatchReportLauncherAdapter.launch`

Step:

- Name: `reportExecutionBatchStep`
- Type: chunk step
- Chunk size: `ReportBatchProperties.chunkSize`, minimum `1`, default from config `reporting.batch.chunk-size=1000`
- Reader: step-scoped `DelegatingReportItemReader`
- Processor: step-scoped `DelegatingReportItemProcessor`
- Writer: step-scoped `DelegatingReportItemWriter`
- Listener: `ReportBatchStepCompletionListener`

Factories and registries:

- `BatchReportLauncherAdapter` creates `JobParameters`.
- `ReportBatchContextFactory` reads job parameters and creates `ReportBatchContext`.
- `ReportBatchSharedStateRegistry.bind` stores the context shared-state map by report execution id.
- `ReportBatchComponentRegistry` selects concrete reader/processor/writer.
- `ReportFormatHandlerRegistry` selects concrete formatter.

Execution order:

1. `ReportExecutionServiceImpl.executeReport` calls `ReportBatchLauncherPort.launch`.
2. `BatchReportLauncherAdapter.launch` serializes parameters and calls `jobLauncher.run(reportExecutionBatchJob, jobParameters)`.
3. Spring Batch starts `reportExecutionBatchJob`.
4. Spring creates step-scoped `ReportBatchContext` through `ReportBatchContextFactory`.
5. `ReportBatchContextFactory` loads `ReportDefinition`, loads `ReportExecution`, parses parameters JSON, initializes shared state, and returns context.
6. `ReportBatchJobConfiguration.reportBatchContext` binds shared state into `ReportBatchSharedStateRegistry`.
7. Step-scoped reader factory asks `ReportBatchComponentRegistry.readerFor`; current result is `JdbcReportBatchReader`.
8. Step-scoped processor factory asks `processorFor`; current result is `GenericReportBatchProcessor`.
9. Step-scoped writer factory asks `writerFor`; current result is `GenericReportBatchWriter`.
10. Batch calls `DelegatingReportItemReader.open`, which calls `JdbcReportBatchReader.open`.
11. `JdbcReportBatchReader.open` validates SQL, loads default template, initializes preview rows, converts named parameters to JDBC placeholders, opens connection/prepared statement/result set.
12. For each row, `JdbcReportBatchReader.read` maps result-set columns to a `LinkedHashMap`, stores column names once, appends up to preview limit, and returns the row.
13. `DelegatingReportItemProcessor.process` calls `GenericReportBatchProcessor.process`.
14. Processor increments `PROCESSED_ROW_COUNT`, enforces `maxExportRows`, and appends row to `ACCUMULATED_ROWS`.
15. Batch chunks rows and calls `DelegatingReportItemWriter.write`.
16. `GenericReportBatchWriter.write` does nothing; rows are already accumulated.
17. When the cursor is exhausted, Batch closes the reader.
18. Batch calls `ReportBatchStepCompletionListener.afterStep`.
19. Listener reads shared state, builds `ReportFormatRequest`, calls `ReportFormatService.format`.
20. Formatter returns `ReportFormatResult` containing the entire output as `byte[]`.
21. Listener writes bytes to local storage, persists `ReportFileEntity`, updates shared `ReportExecution.generatedFileId`, and stores `FORMAT_RESULT`.
22. Job completes.
23. `BatchReportLauncherAdapter` checks status, releases shared state from registry, and returns it to `ReportExecutionServiceImpl`.

SharedState keys:

- `exportMode`
- `reportDefinition`
- `reportExecution`
- `reportFormat`
- `parameters`
- `reportTemplate`
- `previewRows`
- `columnNames`
- `processedRowCount`
- `accumulatedRows`
- `formatResult`

## 6. Reporting Flow

Actual endpoint path is `POST /api/v1/report-execution/execute`.

1. Client sends `ExecuteReportCommand` to `ReportExecutionController.executeReport`.
2. Controller delegates to `ReportExecutionExecuteCommandHandler.executeReport`.
3. Handler delegates to `ReportExecutionServiceImpl.executeReport`.
4. Service loads report definition through `ReportDefinitionServiceImpl` and `IReportDefinitionRepository`.
5. Service ensures the definition is active.
6. Service loads report parameter definitions through `IReportParameterRepository`.
7. `ReportParameterBindingService` resolves and validates request parameters.
8. Service performs report-specific validation for `LoanPortfolioReportService`.
9. Service creates and saves `ReportExecution` with status `RUNNING`.
10. Service records `VALIDATE` execution detail.
11. `ReportSqlGuard` validates the SQL as selectable.
12. Service records `EXECUTE_SQL` as running.
13. `ReportBatchFormatResolver` resolves requested format. Only JSON and CSV are actually export-supported.
14. Service calls `BatchReportLauncherAdapter.launch`.
15. Batch executes the job/step described in section 5.
16. `ReportBatchStepCompletionListener` formats and stores the file, and places generated file id in shared state.
17. Batch launcher releases shared state and returns it.
18. Service copies generated file id from shared state into the saved execution.
19. Service determines total records from `PROCESSED_ROW_COUNT`; if the processed count exceeds the configured row limit path, it executes dynamic count SQL.
20. Service finalizes execution as `COMPLETED`, records `EXECUTE_SQL` completed and `FINALIZE` completed.
21. `ReportExecutionUrlFactory` builds the download URL.
22. `ReportExecutionMapper` builds `ExecuteReportResponse`.
23. Controller returns a standard success response.

Download flow:

1. Client calls `GET /api/v1/report-execution/{reportExecutionId}/download`.
2. `ReportExecutionController.downloadReportFile` delegates to `ReportExecutionDownloadCommandHandler`.
3. Handler calls `ReportExecutionServiceImpl.downloadReportFile`.
4. Service loads `ReportExecution`, verifies status is `COMPLETED`, verifies generated file id exists.
5. Service loads `ReportFile` metadata through `IReportFileRepository`.
6. `LocalReportFileStorageAdapter.readContent` reads the complete local file into `byte[]`.
7. Service returns `ReportFileDownloadResponse`.
8. Controller returns `ResponseEntity<byte[]>` with `Content-Disposition` attachment and stored content type.

## 7. Report Formatting

Format enum:

- `JSON`: declared and implemented.
- `CSV`: declared and implemented.
- `XLSX`: declared and implemented through streaming EasyExcel writing.

Jasper:

- Not implemented.
- No JasperReports dependency or imports were found.
- `output_type` values must resolve to concrete implemented formats such as `JSON`, `CSV`, or `XLSX`.

Formatter classes:

- `ReportFormatService`: facade called by `ReportBatchStepCompletionListener`.
- `ReportFormatHandlerRegistry`: injects all handlers and selects the first where `supports(format)` is true.
- `ReportFormatHandler`: strategy interface.
- `JsonReportFormatHandler`: builds a JSON object with `reportCode`, `executionId`, `parameters`, and all rows, then serializes to UTF-8 `byte[]`.
- `CsvReportFormatHandler`: resolves columns, builds CSV with `StringBuilder`, escapes all fields, then converts to UTF-8 `byte[]`.
- `ReportTemplateColumnResolver`: uses `ReportTemplate.columnsJson` when present; otherwise infers columns from the first row.
- `ReportFormatRequest`: carries definition, execution, template, format, rows, and parameters.
- `ReportFormatResult`: carries file name, extension, content type, and file bytes.
- `ReportFormatColumn`: describes output field/title.

Who creates the file:

- Formatter creates only bytes and metadata.
- `ReportBatchStepCompletionListener.storeResult` writes the byte array to local filesystem under `report-output/report-executions/{executionId}/{fileName}` by default.
- The same listener persists `ReportFileEntity` metadata with checksum, storage provider `LOCAL`, bucket name, object name, size, extension, and content type.

## 8. Database Access

Spring Data repositories:

- `ReportDefinitionRepository`: `JpaRepository`, `JpaSpecificationExecutor`; methods `findByReportCode`, `findByActiveTrue`.
- `ReportExecutionRepository`: `findByReportDefinitionIdOrderByExecutionStartTimeDesc`.
- `ReportExecutionDetailRepository`: `findByReportExecutionIdOrderByStartTimeAsc`.
- `ReportFileRepository`: CRUD only.
- `ReportParameterRepository`: `findByReportDefinitionIdOrderByDisplayOrderAsc`.
- `ReportTemplateRepository`: default/template-name lookup.
- `ReportScheduleRepository`: `findByActiveTrue`, `findByReportDefinitionId`.
- `ReportEventRepository`: `findByEventId`.
- `AccountReportRepository`, `CardReportRepository`, `LoanReportRepository`, `PartyReportRepository`, `ProductReportRepository`, `TransactionReportRepository`: `findByAggregateId`.
- `InstallmentReportRepository`: `findByAggregateId`, `findByLoan_Id`.

`JdbcTemplate` usage:

- Direct `JdbcTemplate` bean injection was not found.
- `ReportQueryExecutorAdapter` uses `NamedParameterJdbcTemplate.getJdbcTemplate().query(...)`.

`NamedParameterJdbcTemplate` usage:

- `ReportQueryExecutorAdapter` injects `NamedParameterJdbcTemplate`.
- It manually parses named parameters, substitutes placeholders, builds value arrays, creates a prepared statement, sets query timeout/max rows, binds values, and accumulates result rows into a list.

Manual JDBC usage:

- `JdbcReportBatchReader` injects `DataSource`.
- It uses `NamedParameterUtils` to convert named SQL parameters to JDBC placeholders.
- It opens `Connection`, `PreparedStatement`, and forward-only/read-only `ResultSet`.
- It sets fetch size to chunk size and query timeout from the report definition.
- It reads rows as maps and converts values via `ReportJdbcValueConverter`.

Native SQL execution:

- Report SQL stored in `ReportDefinition.sqlQuery` is executed as native SQL by `JdbcReportBatchReader`.
- Count SQL is generated in `ReportExecutionServiceImpl` as:

```sql
SELECT COUNT(*) AS cnt FROM (<definition.sqlQuery>) report_count_query
```

Dynamic SQL execution:

- All report execution SQL is dynamic because it comes from persisted report definitions.
- Parameter binding is prepared-statement based after named-parameter substitution.
- `ReportSqlGuard` reduces risk by allowing selectable statements only, but the system still executes user/configured SQL text at runtime.

JPA Criteria/Specification:

- `ReportDefinitionSpecification.from` builds dynamic JPA predicates for report definition search.

## 9. Shared State Analysis

`ReportBatchContext` contains:

- `ReportDefinition reportDefinition`
- `ReportExecution reportExecution`
- `ReportFormat reportFormat`
- `Map<String,Object> parameters`
- `int chunkSize`
- `boolean exportMode`
- `ConcurrentMap<String,Object> sharedState`

`ReportBatchSharedStateRegistry`:

- Backed by `ConcurrentHashMap<Long, ConcurrentMap<String,Object>>`.
- `bind` stores state for an execution id.
- `get` lets the step listener access state during the step.
- `release` removes state after job completion or failure.
- Data stays in JVM memory until released; a crash loses it.

Shared-state writes and reads:

- `EXPORT_MODE`: written by `ReportBatchContextFactory`; read by `ReportBatchStepCompletionListener`.
- `REPORT_DEFINITION`: written by `ReportBatchContextFactory`; read by reader, processor, writer, listener.
- `REPORT_EXECUTION`: written by `ReportBatchContextFactory`; read by processor/listener/service after return.
- `REPORT_FORMAT`: written by `ReportBatchContextFactory`; read by writer/listener.
- `PARAMETERS`: written by `ReportBatchContextFactory`; read by reader/listener.
- `REPORT_TEMPLATE`: written by `JdbcReportBatchReader.open`; read by `ReportBatchStepCompletionListener`.
- `PREVIEW_ROWS`: initialized and appended by `JdbcReportBatchReader`; currently no production reader found after batch completion.
- `COLUMN_NAMES`: written once by `JdbcReportBatchReader`; currently no production reader found after batch completion.
- `PROCESSED_ROW_COUNT`: written by `GenericReportBatchProcessor`; read by `ReportExecutionServiceImpl.totalRecords`.
- `ACCUMULATED_ROWS`: written by `GenericReportBatchProcessor`; read by `ReportBatchStepCompletionListener`.
- `FORMAT_RESULT`: written by `ReportBatchStepCompletionListener`; not required by execution flow after file id is set.

Memory lifetime:

- State is in memory during the whole Batch job.
- `BatchReportLauncherAdapter.release` removes it only after `jobLauncher.run` returns.
- On exceptions, launcher also calls `release`.
- Because the job is synchronous, the HTTP request thread waits while all state remains in memory.

## 10. Memory Analysis

Lists that continuously grow:

- `ACCUMULATED_ROWS` grows by one `Map<String,Object>` per exported row.
- `ReportQueryExecutorAdapter.executeQuery` accumulates all returned rows into `List<Map<String,Object>>`, bounded only by `maxRows` when provided.
- `CsvReportFormatHandler` grows a single `StringBuilder` for the entire CSV file.
- `JsonReportFormatHandler` creates one JSON payload containing all rows.

Objects stored in memory:

- `ReportBatchSharedStateRegistry.states` stores one map per running execution.
- `ReportBatchContext.sharedState` stores definition, execution, parameters, template, preview rows, column names, count, accumulated rows, and format result.
- Each row is represented as a `LinkedHashMap`.
- Formatter output is a `byte[]` in `ReportFormatResult`.
- Download uses another `byte[]` from `Files.readAllBytes`.

Potential OutOfMemory risks:

- Large exports hold every row in `ACCUMULATED_ROWS`.
- CSV generation duplicates row data into a `StringBuilder`, then duplicates it again into `byte[]`.
- JSON generation wraps rows into a payload map and serializes the entire payload into `byte[]`.
- `Files.write` writes the whole byte array; no streaming writer is used.
- `Files.readAllBytes` loads the whole report file for download.
- Concurrent report executions multiply all of the above.

Objects copied multiple times:

- JDBC row -> `LinkedHashMap`.
- `LinkedHashMap` -> `ACCUMULATED_ROWS`.
- Accumulated rows -> formatter string/object payload.
- Formatter output -> `byte[]`.
- `byte[]` -> filesystem.
- Filesystem -> new `byte[]` on download.
- Controller response writes that byte array to HTTP.

Preview buffers:

- `PREVIEW_ROWS` is capped at 50 or `maxExportRows`, whichever is smaller.
- It is still an additional reference list for the first rows.

In-memory report generation:

- Current JSON and CSV generation are fully in-memory.
- No streaming CSV writer, streaming JSON writer, temporary file writer, or HTTP streaming response is used.

## 11. Dead Code Analysis

Unused or weakly used classes/abstractions:

- `GenericReportBatchWriter` is structurally used by Batch but performs no write; it exists only to satisfy Spring Batch writer contract.
- `PREVIEW_ROWS` and `COLUMN_NAMES` are written but no production reader was found.
- `FORMAT_RESULT` is stored in shared state but the main service only needs generated file id from `REPORT_EXECUTION`.
- `ReportCategory`, `ReportCategoryId`, `ReportCategoryEntity`, and `ReportCategoryDataAccessMapper` are scaffold-only; no repository, adapter, or runtime caller was found.
- Domain `ReportSchedule` and `ReportScheduleDataAccessMapper` are scaffold-only; scheduling uses `ReportScheduleEntity` and repositories directly.
- Domain `ReportEvent` and `ReportEventDataAccessMapper` are scaffold-only; event persistence uses `ReportEventEntity` directly.
- `ReportType` enum is unused; report type is represented as a `String`.
- `EventStatus.RETRIED` is declared but not set by current code.
- `BaseResponse`/`ErrorDetail` coexist with platform `StandardResponse`; this is a duplicate response abstraction.
- `IReportEventPersistenceAdapter` and `IReportProjectionPersistenceAdapter` are named like interfaces but are concrete adapter classes.
- Each projection service interface has exactly one implementation and mostly forwards to a repository port.
- Each projection event handler is thin and differs mainly by aggregate type/service.
- Batch component interfaces currently have one concrete reader, one concrete processor, and one concrete writer.
- Streaming batch writer abstraction now owns concrete JSON, CSV, and XLSX output generation.
- `ReportFormatService` is a very thin facade over `ReportFormatHandlerRegistry`.

Unused implementations:

- No Jasper formatter implementation exists.
- No Rabbit publisher path was found; messaging is consumer-only.

Unused configuration:

- `ReportScheduleEntity.outputType` is not passed into `ExecuteReportCommand` by `ReportScheduleExecutionJob`, so scheduled runs fall back to command/default definition format resolution.
- JasperReports is mentioned by requirements but absent from code/runtime.

Duplicate code:

- CSV and JSON handlers both implement similar timestamped sanitized file-name construction.
- Projection service implementations and projection event handlers follow repetitive one-implementation patterns.
- JPA mapper/repository adapter patterns are repeated for each aggregate.

Classes with one implementation that can be simplified:

- `ReportExecutionService`, `ReportDefinitionService`, all projection input ports, all output ports, `ReportBatchReader`, `ReportBatchProcessor`, `ReportBatchWriter`, `ReportFormatHandler` currently have limited or single implementation in this service.
- Keep ports where module boundaries and testing justify them; simplify projection-specific ports if they remain pass-through.

## 12. Runtime Sequence Diagram

```text
Client
  ↓ POST /api/v1/report-execution/execute
ReportExecutionController
  ↓
ReportExecutionExecuteCommandHandler
  ↓
ReportExecutionServiceImpl
  ↓ load definition, parameters, validate SQL, save execution
ReportDefinitionServiceImpl / repository ports / JPA adapters
  ↓
BatchReportLauncherAdapter
  ↓ JobLauncher.run(reportExecutionBatchJob)
Spring Batch Job
  ↓
reportExecutionBatchStep
  ↓ create ReportBatchContext and bind SharedState
ReportBatchContextFactory + ReportBatchSharedStateRegistry
  ↓
ReportBatchComponentRegistry
  ↓
DelegatingReportItemReader
  ↓
JdbcReportBatchReader
  ↓ stream ResultSet rows as Map<String,Object>
DelegatingReportItemProcessor
  ↓
GenericReportBatchProcessor
  ↓ count rows and append ACCUMULATED_ROWS
DelegatingReportItemWriter
  ↓
GenericReportBatchWriter no-op
  ↓ after step
ReportBatchStepCompletionListener
  ↓
ReportFormatService
  ↓
ReportFormatHandlerRegistry
  ↓
JsonReportFormatHandler or CsvReportFormatHandler
  ↓ returns ReportFormatResult byte[]
ReportBatchStepCompletionListener
  ↓ Files.write + ReportFileRepository.save
Local Storage + Database metadata
  ↓ sharedState REPORT_EXECUTION.generatedFileId
BatchReportLauncherAdapter.release(sharedState)
  ↓
ReportExecutionServiceImpl finalizes execution
  ↓
Client receives ExecuteReportResponse with download URL
  ↓ GET /api/v1/report-execution/{id}/download
ReportExecutionController
  ↓
ReportExecutionDownloadCommandHandler
  ↓
ReportExecutionServiceImpl
  ↓
ReportFilePersistenceAdapter + LocalReportFileStorageAdapter
  ↓ Files.readAllBytes
Client downloads file
```

## 13. Improvement Suggestions

| Priority | Problem | Reason | Impact | Suggested fix |
|---|---|---|---|---|
| High | Export generation is fully in-memory. | Rows, CSV/JSON payload, and bytes are accumulated before write. | OOM risk and poor scalability. | Stream rows directly to a temp file/output stream; avoid `ACCUMULATED_ROWS` for exports. |
| High | Download loads full file into memory. | `Files.readAllBytes` and `ResponseEntity<byte[]>` buffer entire file. | OOM risk for large reports. | Return `Resource`, `InputStreamResource`, or streaming response. |
| Medium | Format support must remain aligned with streaming writers. | Each enum value needs a matching batch writer. | Runtime `Unsupported report format` failures if they drift. | Keep `ReportFormat`, resolver, and streaming writer registry in sync. |
| High | Dynamic SQL is persisted and executed. | SQL comes from report definitions. | Security and operational risk. | Strengthen SQL allowlist/parser, restrict data source permissions, add timeout/row limits everywhere, audit SQL changes. |
| Medium | Batch writer is a no-op. | Actual writing occurs in processor/listener via shared state. | Confusing Batch model and weak restart semantics. | Move file streaming to an `ItemWriter` and use listener only for metadata finalization. |
| Medium | Shared state is JVM-local. | Registry uses in-memory map. | No crash recovery; not cluster safe. | Persist execution state or keep Batch execution self-contained without external registry. |
| Medium | Count query wraps dynamic SQL. | Generated SQL may be expensive or invalid with complex queries. | Slow finalization or DB load. | Use processed count for export-limited runs or optional count strategy per report. |
| Medium | Scheduler executes reports synchronously. | Scheduled job calls same blocking execution path. | Long schedule scan, missed/late schedules. | Launch asynchronous jobs and persist schedule run status. |
| Medium | Schedule output format is ignored. | `ReportScheduleExecutionJob` builds `ExecuteReportCommand` without setting `reportFormat`. | Scheduled exports may fail or use unintended default format. | Map `ReportScheduleEntity.outputType` to an implemented `ReportFormat` or require explicit schedule format validation. |
| Medium | Infrastructure bypasses application ports in places. | `ReportBatchStepCompletionListener` writes through `ReportFileRepository` directly; scheduler uses JPA entities directly. | Weaker hexagonal boundaries and harder testing. | Route file metadata through an output port and introduce a schedule application port/use case. |
| Medium | Projection services/handlers are repetitive. | One interface/implementation per aggregate. | Maintenance overhead. | Consolidate generic projection upsert logic where possible. |
| Low | File-name generation duplicated. | CSV/JSON handlers duplicate sanitize/timestamp code. | Minor drift risk. | Extract `ReportFileNameFactory`. |
| Low | Response wrappers are duplicated. | Local `BaseResponse` and platform `StandardResponse` coexist. | Inconsistent API responses. | Standardize on one response contract. |

## 14. Delete Candidates

| Class | Reason | Safe to Delete | Replacement |
|---|---|---|---|
| `GenericReportBatchWriter` | No-op writer. | No, Batch step currently requires writer bean. | Streaming report `ItemWriter`. |
| `ReportFormatService` | Thin facade over registry. | Yes, if callers use registry directly. | `ReportFormatHandlerRegistry.findHandler(...).format(...)`. |
| `BaseResponse` | Duplicates platform response style. | No, exception handler uses it. | `StandardResponse`-based error response. |
| `ErrorDetail` | Tied to `BaseResponse`. | No, exception handler uses it. | Platform error contract. |
| `ReportCategory` stack | No repository, adapter, API, or runtime caller found for `ReportCategory`, `ReportCategoryId`, `ReportCategoryEntity`, or `ReportCategoryDataAccessMapper`. | No, confirm database/Flyway dependencies first. | Existing `ReportDefinitionEntity.category` string field. |
| Domain `ReportSchedule` stack | Scheduler uses `ReportScheduleEntity` directly, not domain `ReportSchedule` or mapper. | No, confirm future schedule API plans first. | Schedule application use case or direct JPA entity path. |
| Domain `ReportEvent` stack | Event adapter persists `ReportEventEntity` directly; domain `ReportEvent` and mapper are unused. | No, confirm event audit API plans first. | Current `IReportEventPersistenceAdapter` entity mapping. |
| `ReportType` | Enum is unused while report type is represented as `String`. | Yes, if no external API/import uses it. | Existing string field or migrate fully to enum. |
| Projection service interfaces | One implementation each and mostly pass-through. | No, unless architecture accepts fewer ports. | Generic projection service or direct output port use. |
| `IReportEventPersistenceAdapter` | Concrete class with interface-like name. | No. | Rename to `ReportEventPersistenceAdapter`. |
| `IReportProjectionPersistenceAdapter` | Concrete class with interface-like name. | No. | Rename to `ReportProjectionPersistenceAdapter`. |

## 15. Overall Architecture Score

| Category | Score | Notes |
|---|---:|---|
| Layering | 7/10 | Clear modules and ports; infrastructure concerns leak through Batch shared state and scheduler using JPA entities directly. |
| SOLID | 6/10 | Interfaces and strategies exist, but many one-implementation abstractions and pass-through handlers add noise. |
| Spring Batch usage | 5/10 | Job/step structure works, but writer is no-op, rows accumulate in processor, and listener performs file generation. |
| Memory usage | 3/10 | Exports and downloads are fully buffered in memory. |
| Performance | 5/10 | JDBC cursor/fetch size is good, but formatter/file/download buffering limits large reports. |
| Scalability | 4/10 | Synchronous HTTP execution and JVM-local shared state limit horizontal scaling and long-running jobs. |
| Maintainability | 6/10 | Package boundaries are understandable, but duplicated projection/report-format patterns and unsupported formats need cleanup. |

Overall score: 5.1/10.
