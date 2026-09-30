package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.CardReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardReportRepository extends JpaRepository<CardReportEntityQuery, Long> {

    Optional<CardReportEntityQuery> findByAggregateId(String aggregateId);
}
