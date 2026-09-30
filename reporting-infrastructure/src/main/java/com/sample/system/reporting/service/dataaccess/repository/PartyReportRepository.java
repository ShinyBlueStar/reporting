package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.PartyReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyReportRepository extends JpaRepository<PartyReportEntityQuery, Long> {

    Optional<PartyReportEntityQuery> findByAggregateId(String aggregateId);
}
