package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.ReportQueryExecutorPort;
import com.sample.system.reporting.service.batch.ReportJdbcValueConverter;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ReportQueryExecutorAdapter implements ReportQueryExecutorPort {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void executeQuery(String sql,
                             Map<String, Object> parameters,
                             Integer timeoutSeconds,
                             int maxRows,
                             Consumer<Map<String, Object>> consumer) {
        try {
            Map<String, Object> safeParams = parameters != null ? parameters : Map.of();
            SqlParameterSource parameterSource = new MapSqlParameterSource(safeParams);
            ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
            String preparedSql = NamedParameterUtils.substituteNamedParameters(parsedSql, parameterSource);
            Object[] paramValues = NamedParameterUtils.buildValueArray(parsedSql, parameterSource, null);

            namedParameterJdbcTemplate.getJdbcTemplate().query(connection -> {
                // Report SQL is user supplied: never let it write, even if the guard is bypassed.
                // HikariCP resets the read-only flag when the connection goes back to the pool.
                connection.setReadOnly(true);
                PreparedStatement statement = connection.prepareStatement(preparedSql);
                if (timeoutSeconds != null && timeoutSeconds > 0) {
                    statement.setQueryTimeout(timeoutSeconds);
                }
                if (maxRows > 0) {
                    statement.setMaxRows(maxRows);
                }
                for (int i = 0; i < paramValues.length; i++) {
                    bindParameter(statement, i + 1, paramValues[i]);
                }
                return statement;
            }, rs -> {
                int columnCount = rs.getMetaData().getColumnCount();
                int rowCount = 0;
                while (rs.next() && (maxRows <= 0 || rowCount < maxRows)) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = rs.getMetaData().getColumnLabel(i);
                        row.put(columnLabel, ReportJdbcValueConverter.convert(rs.getObject(i)));
                    }
                    consumer.accept(row);
                    rowCount++;
                }
                return null;
            });
        } catch (RuntimeException ex) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    ex.getMessage() != null ? ex.getMessage() : ErrorCode.REPORT_EXECUTION_FAILED.name());
        }
    }

    private void bindParameter(PreparedStatement statement, int index, Object value) throws SQLException {
        statement.setObject(index, ReportJdbcValueConverter.toJdbcParameter(value));
    }
}
