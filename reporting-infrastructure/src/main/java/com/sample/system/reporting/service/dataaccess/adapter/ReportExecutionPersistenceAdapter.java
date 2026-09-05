package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportExecutionRepository;
import com.sample.system.reporting.service.dataaccess.mapper.ReportExecutionDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ReportExecutionRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportExecutionPersistenceAdapter implements IReportExecutionRepository {

    private final ReportExecutionRepository reportExecutionRepository;
    private final ReportExecutionDataAccessMapper reportExecutionDataAccessMapper;

    @Override
    public ReportExecution save(ReportExecution reportExecution) {
        return reportExecutionDataAccessMapper.reportExecutionEntityToReportExecution(
                reportExecutionRepository.save(
                        reportExecutionDataAccessMapper.reportExecutionToReportExecutionEntity(reportExecution)));
    }

    @Override
    public ReportExecution findById(Long id) {
        return reportExecutionRepository.findById(id)
                .map(reportExecutionDataAccessMapper::reportExecutionEntityToReportExecution)
                .orElse(null);
    }
}
