package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportExecutionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportExecutionDetailRepository extends JpaRepository<ReportExecutionDetailEntity, Long> {

    List<ReportExecutionDetailEntity> findByReportExecutionIdOrderByStartTimeAsc(Long reportExecutionId);
}
