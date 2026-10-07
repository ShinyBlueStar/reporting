# Reporting Service

A configurable, event-driven reporting platform for a banking system. Business users define reports as data
(SQL, parameters, validation rules, output templates); the service runs them safely, exports them as CSV, JSON or
Excel, stores the files in S3-compatible object storage, and keeps its own read models up to date from RabbitMQ events.

I designed and built this service end to end: the architecture, the domain model, the security model, the export
pipeline, the messaging reliability, the storage layer, the container setup and the test suite. This document explains
what it does, why it is built this way, and where its limits are.

---

## At a glance

| | |
|---|---|
| **Stack** | Java 21, Spring Boot 4.0.1, Spring Batch, Spring Data JPA (Hibernate + Envers), Spring Security, Oracle, RabbitMQ, Redis, S3 API |
| **Size** | 7 Maven modules, ~325 production classes (~13k lines), 51 test classes, **163 automated tests, all green** |
| **Architecture** | Hexagonal / clean architecture with strict module boundaries (domain has no framework dependencies on infrastructure) |
| **Operations** | Dockerfile, Docker Compose (Oracle, RabbitMQ, Redis, S3-compatible store), optional Prometheus/Grafana and ELK profiles |

---

## What the service does

1. **Report definitions as data.** A report is a record: code, name, SQL query, output type, timeout, export row limit,
   typed parameters, validation rules and an output template. Adding a new report needs no code change or deployment.
2. **Safe execution.** Every query is parsed and validated before it is stored or run (details below), executed with
   named parameters only, with a timeout and a row cap.
3. **Validation engine.** More than 25 configurable rule types (required, conditional required, length, regex, date
   range, in/not-in list, exactly-one, all-or-none, uniqueness and existence checks against the database, and more).
   All violations are collected and returned together, not one at a time.
4. **Streaming export.** Reports are produced through a Spring Batch pipeline (reader, processor, writer) that streams
   rows to **CSV, JSON or XLSX** without loading the result set into memory. XLSX output supports right-to-left sheets
   for Persian-language reports. Column titles and order come from a per-report template.
5. **Object storage.** Finished files are uploaded to any S3-compatible store (AWS S3, SeaweedFS, Ceph, Garage,
   MinIO). Expiry is handled by a bucket lifecycle rule, so it works with many service instances.
6. **Event-driven read models.** Party, card, loan, installment, account, transaction and product events arrive over
   RabbitMQ and are projected into dedicated reporting tables, so reports never query other services' databases.
7. **Scheduling.** Reports can run on a cron schedule. Several instances can run side by side: each due schedule is
   claimed with an atomic compare-and-set update, so it executes exactly once.
8. **Audit trail.** Hibernate Envers history for the report engine's tables, plus created/modified metadata.

---

## Architecture

```
reporting-domain
 ├─ reporting-domain-core           entities, value objects, enums, domain exceptions (no Spring web/infra code)
 └─ reporting-application-service   use cases, commands/handlers, ports, validation engine, batch contracts
reporting-application               REST controllers, error handling, response envelope
reporting-infrastructure            JPA adapters, Spring Batch pipeline, writers, S3/local storage, schedulers
reporting-messaging                 RabbitMQ topology and consumers (retry + dead-letter)
reporting-container                 Spring Boot entry point, configuration, security, profiles, migrations
```

The application layer talks to the outside world only through **ports** (`IReportDefinitionRepository`,
`ReportQueryExecutorPort`, `ReportFileStoragePort`, `ReportBatchLauncherPort`, ...). Infrastructure provides the
adapters. That is why storage could move from local disk to S3, and why the export engine can be tested without a
database, without changing a single use case.

---

## Engineering decisions worth calling out

### Security
- **Authentication and authorization.** Stateless JWT resource server. Tokens are verified against a JWK set (any
  OIDC provider such as Keycloak) or, for local development only, a shared HMAC secret. Roles are read from a
  configurable claim and from Keycloak's `realm_access`.
- **Least privilege.** `REPORT_USER` can run reports, download results and read definitions. Only `REPORT_ADMIN` can
  create, change or delete definitions, parameters, templates and rules, because a definition carries SQL.
- **Identity is never trusted from the request body.** The executing user is taken from the token.
- **SQL guard.** Report SQL is parsed (JSqlParser), not pattern-matched. Only a single `SELECT`/`WITH` statement is
  accepted; `FOR UPDATE`, `SELECT INTO`, database links and privileged Oracle packages (`DBMS_*`, `UTL_*`, `SYS.*`)
  are rejected, and keywords inside literals or comments neither trigger nor hide a match. Execution also marks the
  connection read-only.
- **File access.** Object names are validated against path traversal for both local and S3 storage.
- **Errors never leak internals.** Unexpected exceptions return a generic message; the detail goes to the log.

### Reliability
- **Messaging.** Manual acknowledgements. Transient failures go through a delay queue and are retried a configurable
  number of times; malformed messages and exhausted retries go to a dead-letter queue. The retry message is published
  before the original is acknowledged, so a failure while retrying cannot lose the event.
- **Scheduling.** All schedule times are UTC; one broken schedule (for example an invalid cron expression) is logged
  and skipped and never blocks the others.
- **Export limits.** Row caps (configurable per report, default 20,000) stop runaway exports.

### Observability
- Every request carries a correlation id (`X-Request-Id`, validated and echoed back) that appears in each log line.
- Structured ECS JSON logging via the `json` profile, ready for Filebeat/Logstash and Elasticsearch.
- Prometheus metrics at `/actuator/prometheus`; liveness/readiness probes.
- A step-by-step execution trace is stored per report run (`report_execution_detail`).

### Maintainability
- Modern Java 21 (records for configuration and value types, pattern matching, text blocks).
- Dependencies are managed by the Spring Boot BOM; no private artifact repository is required to build.
- The database scripts under `db/migration` (V1-V6) were generated from the entity model and reviewed; they are
  validated by a test that runs them against an in-memory database in Oracle mode.

---

## Testing

163 tests run with `mvn verify`. They concentrate on the critical paths and deliberately skip pure mapping code:

- SQL guard (accepted and rejected statements, literals/comments, links, packages)
- Validation engine and rules, parameter binding, date parsing
- Report execution orchestration and step tracking
- Streaming writers (CSV escaping, JSON structure, file naming), batch components, row limits
- Query executor and persistence adapters
- Storage adapters (path traversal, upload, bucket handling, failure paths)
- RabbitMQ topology, retry and dead-letter behaviour
- Scheduler (claiming, invalid cron isolation) and file cleanup
- Security (role mapping, startup validation) and end-to-end authorization (anonymous / user / admin)
- Flyway scripts against an Oracle-mode database

---

## Running it

Requirements: JDK 21, Maven 3.9+, Docker (for the full stack).

```bash
mvn clean verify                               # build and run all tests

cp .env.example .env                           # adjust secrets first
docker compose up --build                      # app + Oracle + RabbitMQ + Redis + S3-compatible storage
docker compose --profile monitoring up         # + Prometheus and Grafana
docker compose --profile elk up                # + Elasticsearch, Kibana, Filebeat (use the "json" profile)
```

Useful endpoints (default port `8021`): `/swagger-ui.html`, `/actuator/health`, `/actuator/prometheus`.

Key settings (environment variables): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `RABBITMQ_*`, `REDIS_*`,
`S3_ENDPOINT`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_BUCKET`, `REPORTING_STORAGE_TYPE` (`s3` or `local`),
`REPORTING_JWT_JWK_SET_URI` or `REPORTING_JWT_HMAC_SECRET`, `REPORTING_SECURITY_ENABLED`.

---

## Honest status and next steps

I would rather state the limits than let someone discover them:

- **Verified by automated tests, not yet by a live full stack.** The Docker Compose setup, the Oracle scripts and the
  S3 integration are covered by unit and in-memory tests; I have not run them against live Oracle, RabbitMQ and an
  S3 server in this repository's history.
- **Flyway is intentionally disabled.** The migration scripts are maintained and tested but applied manually. In
  development the schema is created by Hibernate (`ddl-auto: update`), which should not be used in production.
- **The read-only connection is a hint, not a guarantee.** The reporting database account should be read-only.
- **Complex Oracle SQL that the parser cannot read is rejected** (safe by default); existing reports should be
  re-validated once against a real database.
- **Planned improvements:** map domain error codes to precise HTTP statuses (404/409/422 instead of 500), contract
  tests with real RabbitMQ/S3/Oracle containers, business metrics (executions, duration, failures) and an alert on
  the dead-letter queue, multipart uploads for very large files, and distributed tracing with OpenTelemetry.
