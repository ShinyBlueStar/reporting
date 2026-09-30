package com.sample.system.reporting.service.dataaccess.entity.query;

import com.sample.system.reporting.service.dataaccess.entity.Audit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseReportEntityQuery extends Audit {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "report_seq"
    )
    @SequenceGenerator(
            name = "report_seq",
            sequenceName = "REPORT_SEQ",
            allocationSize = 1
    )
    private Long id;

    /**
     * Aggregate ID from source system
     */
    @Column(name = "aggregate_id", nullable = false, unique = true)
    private String aggregateId;

    /**
     * Version of aggregate
     * Used for out-of-order event protection
     */
    @Column(name = "aggregate_version", nullable = false)
    private Long aggregateVersion;

    /**
     * Last successfully applied event
     */
    @Column(name = "last_event_id", nullable = false, length = 100)
    private String lastEventId;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
}