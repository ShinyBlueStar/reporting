# reporting-service — Architecture Overview

## Architectural Style

**Hexagonal Architecture (Ports & Adapters)** combined with **Clean Architecture** layering and **DDD** tactical patterns.

### Dependency Rule

Dependencies point **inward only**:
- Presentation → Application → Domain
- Infrastructure → Application (implements output ports) → Domain
- Messaging → Application → Domain
- Domain has **zero** dependencies on outer layers

## Layers

| Layer | Module | Count | Responsibility |
|-------|--------|------:|----------------|
| Domain | reporting-domain-core | 0 | Entities, Aggregates, Value Objects, Enums, Domain Exceptions |
| Application | reporting-application-service | 0 | Use Cases, Ports, Commands, Handlers, DTOs, Event Dispatch |
| Presentation | reporting-application | 7 | REST Controllers, API DTOs, Exception Handling |
| Infrastructure | reporting-infrastructure | 82 | JPA, Adapters, Batch, Format Handlers, Schedulers |
| Messaging | reporting-messaging | 3 | RabbitMQ Consumer, Event Ingestion |
| Container | reporting-container | 132 | Spring Boot Bootstrap, Batch Job Config |

## Bounded Contexts

### 1. ReportDefinition (Command Model)
- **Aggregate Root:** `ReportDefinition`
- **Related Entities:** `ReportParameter`, `ReportCategory`, `ReportTemplate`, `ReportSchedule`
- **Repository:** `IReportDefinitionRepository` (port) → `ReportDefinitionPersistenceAdapter`
- **Use Cases:** CRUD + search + status update via CommandHandlers

### 2. ReportExecution (Execution Model)
- **Aggregate Root:** `ReportExecution`
- **Related Entity:** `ReportExecutionDetail`, `ReportFile`
- **Repositories:** `IReportExecutionRepository`, `IReportExecutionDetailRepository`, `IReportFileRepository`
- **Batch Port:** `ReportBatchLauncherPort` → `BatchReportLauncherAdapter` → Spring Batch

### 3. EventSync (CQRS Read Model / Projections)
- **Aggregate Roots (Projections):** `PartyReport`, `CardReport`, `LoanReport`, `AccountReport`, `ProductReport`, `TransactionReport`, `InstallmentReport`
- **Event Store:** `ReportEvent` aggregate + `IReportEventRepository`
- **Flow:** RabbitMQ → `ReportingEventConsumer` → `ReportingSyncService` → `ProjectionEventDispatcher` → Handlers → Projection Services → `IReportProjectionRepository`

### 4. BatchProcessing
- **Contracts:** `ReportBatchReader`, `ReportBatchProcessor`, `ReportBatchWriter`
- **Implementations:** JDBC Reader, Generic Processor/Writer, Delegating Spring Batch adapters

### 5. ReportFormat
- **Strategy Pattern:** `ReportFormatHandler` → `JsonReportFormatHandler`, `CsvReportFormatHandler`
- **Orchestration:** `ReportFormatService` + `ReportFormatHandlerRegistry`

## DDD Decisions

| Decision | Implementation |
|----------|----------------|
| Aggregate Roots | `ReportDefinition`, `ReportExecution`, `ReportEvent`, 7 Projection aggregates |
| Value Objects | 17 `*Id` types extending `BaseId<Long>` |
| Repository per AR | Only aggregate roots have repository ports |
| Domain Events | Ingested via `EventEnvelope` record; idempotency via `IReportEventRepository` |
| Anti-Corruption | `ReportingProjectionMapper`, MapStruct `*DataAccessMapper` |
| Specification | `ReportDefinitionSpecification` for dynamic queries |

## Key Design Patterns

- **CQRS:** Command entities (JPA `entity.command`) vs Query/Projection entities (`entity.query`)
- **Strategy:** Report format handlers, batch reader/processor/writer registries
- **Chain of Responsibility:** `ProjectionEventDispatcherImpl` dispatches to matching handler
- **Template Method:** `AbstractProjectionEventHandler`
- **Adapter:** All `*PersistenceAdapter` implement application output ports
