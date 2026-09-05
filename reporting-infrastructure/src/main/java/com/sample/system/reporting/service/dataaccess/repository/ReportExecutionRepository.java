package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportExecutionRepository extends JpaRepository<ReportExecutionEntity, Long> {

    List<ReportExecutionEntity> findByReportDefinitionIdOrderByExecutionStartTimeDesc(Long reportDefinitionId);
}
