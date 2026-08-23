package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.ValidationQueryPort;
import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class ValidationQueryAdapter implements ValidationQueryPort {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ReportSqlGuard reportSqlGuard;

    @Override
    public long executeCount(String sql, Map<String, Object> parameters) {
        reportSqlGuard.ensureSelectableQuery(sql);
        AtomicReference<Object> countValue = new AtomicReference<>();
        try {
            namedParameterJdbcTemplate.query(
                    sql,
                    parameters,
                    rs -> {
                        if (rs.next()) {
                            countValue.set(rs.getObject(1));
                        }
                    });
        } catch (RuntimeException ex) {
            throw new ReportingDomainException(
                    ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                    "Validation query execution failed");
        }
        Object count = countValue.get();
        if (count instanceof Number number) {
            return number.longValue();
        }
        if (count == null) {
            return 0L;
        }
        return Long.parseLong(count.toString());
    }
}
