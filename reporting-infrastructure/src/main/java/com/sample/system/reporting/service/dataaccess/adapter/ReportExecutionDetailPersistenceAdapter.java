package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportExecutionDetailRepository;
import com.sample.system.reporting.service.dataaccess.mapper.ReportExecutionDetailDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ReportExecutionDetailRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportExecutionDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportExecutionDetailPersistenceAdapter implements IReportExecutionDetailRepository {

    private final ReportExecutionDetailRepository reportExecutionDetailRepository;
    private final ReportExecutionDetailDataAccessMapper reportExecutionDetailDataAccessMapper;

    @Override
    public ReportExecutionDetail save(ReportExecutionDetail detail) {
        return reportExecutionDetailDataAccessMapper.reportExecutionDetailEntityToReportExecutionDetail(
                reportExecutionDetailRepository.save(
                        reportExecutionDetailDataAccessMapper.reportExecutionDetailToReportExecutionDetailEntity(detail)));
    }
}
