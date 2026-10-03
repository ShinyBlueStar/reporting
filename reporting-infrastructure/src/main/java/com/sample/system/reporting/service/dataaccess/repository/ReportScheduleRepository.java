package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface ReportScheduleRepository extends JpaRepository<ReportScheduleEntity, Long> {

    List<ReportScheduleEntity> findByActiveTrue();

    List<ReportScheduleEntity> findByReportDefinitionId(Long reportDefinitionId);

    /**
     * Compare-and-set claim: only the instance whose update touches a row may run the schedule, so several
     * service instances never execute the same due schedule twice.
     */
    @Transactional
    @Modifying
    @Query("""
            update ReportScheduleEntity s set s.nextExecutionTime = :next
            where s.id = :id and s.nextExecutionTime = :expected
            """)
    int claimDueSchedule(@Param("id") Long id, @Param("expected") Instant expected, @Param("next") Instant next);
}
