package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.ReportEventEntity;
import com.sample.system.reporting.service.domain.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportEventRepository extends JpaRepository<ReportEventEntity, Long> {

    Optional<ReportEventEntity> findByEventId(String eventId);

    boolean existsByEventIdAndStatus(String eventId, EventStatus status);
}
