package com.sample.system.reporting.service.dataaccess.entity.query;

import com.sample.system.reporting.service.dataaccess.entity.Audit;
import com.sample.system.reporting.service.domain.model.enums.EventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "report_event",
        indexes = {
                @Index(
                        name = "idx_report_event_aggregate_id",
                        columnList = "aggregate_id"
                ),

                @Index(
                        name = "idx_report_event_event_type",
                        columnList = "event_type"
                ),

                @Index(
                        name = "idx_report_event_timestamp",
                        columnList = "event_timestamp"
                )
        })
public class ReportEventEntity extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_event_seq")
    @SequenceGenerator(name = "report_event_seq", sequenceName = "REPORT_EVENT_SEQ", allocationSize = 1)
    private Long id;

    /**
     * Unique integration event id (broker-agnostic)
     */
    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    /**
     * LoanCreated
     * LoanApproved
     * PartyUpdated
     */
    @Column(name = "event_type",nullable = false, length = 100)
    private String eventType;

    /**
     * LOAN
     * PARTY
     * ACCOUNT
     */
    @Column(name = "aggregate_type")
    private String aggregateType;

    @Column(name = "aggregate_id")
    private String aggregateId;

    @Column(name = "aggregate_version")
    private Long aggregateVersion;

    /**
     * Original event payload
     */
    @Lob
    @Column(name = "payload")
    private String payload;

    /**
     * loan-service
     * account-service
     * card-service
     */
    @Column(name = "source_system", length = 100)
    private String sourceSystem;

    /**
     * Distributed tracing
     */
    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private EventStatus status;

    /**
     * Event creation time from source
     */
    @Column(name = "event_timestamp")
    private Instant eventTimestamp;

    /**
     * When reporting service processed it
     */
    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message", length = 4000)
    private String errorMessage;
}