package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.ports.output.IReportTemplateRepository;
import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchReader;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class JdbcReportBatchReader implements ReportBatchReader<Map<String, Object>> {

    private final DataSource dataSource;
    private final IReportTemplateRepository reportTemplateRepository;
    private final ReportSqlGuard reportSqlGuard;

    private ConcurrentMap<String, Object> sharedState;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private int columnCount;
    private long readCount;

    @Override
    public boolean supports(ReportDefinition reportDefinition) {
        return reportDefinition.getSqlQuery() != null && !reportDefinition.getSqlQuery().isBlank();
    }

    @Override
    public void open(ReportBatchContext context) {
        ReportDefinition definition = context.getReportDefinition();
        sharedState = context.getSharedState();
        readCount = 0L;
        Long executionId = context.getReportExecution().getId().getValue();
        String reportCode = definition.getReportCode();
        log.info("Opening JDBC report reader. executionId={}, reportCode={}, chunkSize={}, timeoutSeconds={}",
                executionId,
                reportCode,
                context.getChunkSize(),
                definition.getTimeoutSeconds());
        reportSqlGuard.ensureSelectableQuery(definition.getSqlQuery());

        ReportTemplate template = reportTemplateRepository.findDefaultByReportDefinitionId(definition.getId().getValue());
        if (template != null) {
            sharedState.put(ReportBatchSharedStateKeys.REPORT_TEMPLATE, template);
            log.debug("Loaded report template for executionId={}, reportCode={}, templateId={}",
                    executionId,
                    reportCode,
                    template.getId() != null ? template.getId().getValue() : null);
        } else {
            log.debug("No report template found for executionId={}, reportCode={}", executionId, reportCode);
        }

        Map<String, Object> parameters = context.getParameters() != null ? context.getParameters() : Map.of();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameters);
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(definition.getSqlQuery());
        String sql = NamedParameterUtils.substituteNamedParameters(parsedSql, parameterSource);
        Object[] paramValues = NamedParameterUtils.buildValueArray(parsedSql, parameterSource, null);

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(
                    sql,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(Math.max(1, context.getChunkSize()));
            applyQueryTimeout(preparedStatement, definition.getTimeoutSeconds());
            bindParameters(preparedStatement, paramValues);
            log.debug("Executing report SQL. executionId={}, reportCode={}, namedParameterCount={}, jdbcParameterCount={}",
                    executionId,
                    reportCode,
                    parameters.size(),
                    paramValues != null ? paramValues.length : 0);
            resultSet = preparedStatement.executeQuery();
            columnCount = resultSet.getMetaData().getColumnCount();
            log.info("Report SQL cursor opened. executionId={}, reportCode={}, columnCount={}",
                    executionId,
                    reportCode,
                    columnCount);
        } catch (SQLException ex) {
            log.error("Failed to open JDBC report reader. executionId={}, reportCode={}, message={}",
                    executionId,
                    reportCode,
                    ex.getMessage(),
                    ex);
            closeQuietly();
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    ex.getMessage() != null ? ex.getMessage() : ErrorCode.REPORT_EXECUTION_FAILED.name());
        }
    }

    @Override
    public Map<String, Object> read() {
        if (resultSet == null) {
            return null;
        }
        try {
            if (!resultSet.next()) {
                log.info("JDBC report reader exhausted. rowsRead={}", readCount);
                return null;
            }
            readCount++;
            Map<String, Object> row = mapRow(resultSet);
            if (readCount == 1 || readCount % 1000 == 0) {
                log.debug("JDBC report reader progress. rowsRead={}", readCount);
            }
            return row;
        } catch (SQLException ex) {
            log.error("Failed to read report row after rowsRead={}. message={}", readCount, ex.getMessage(), ex);
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    ex.getMessage() != null ? ex.getMessage() : ErrorCode.REPORT_EXECUTION_FAILED.name());
        }
    }

    @Override
    public void close() {
        closeQuietly();
        log.info("Closed JDBC report reader. rowsRead={}", readCount);
        sharedState = null;
    }

    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            row.put(rs.getMetaData().getColumnLabel(i), ReportJdbcValueConverter.convert(rs.getObject(i)));
        }
        return row;
    }

    private void applyQueryTimeout(PreparedStatement statement, Integer timeoutSeconds) throws SQLException {
        if (timeoutSeconds != null && timeoutSeconds > 0) {
            statement.setQueryTimeout(timeoutSeconds);
        }
    }

    private void bindParameters(PreparedStatement statement, Object[] paramValues) throws SQLException {
        if (paramValues == null) {
            return;
        }
        for (int i = 0; i < paramValues.length; i++) {
            bindParameter(statement, i + 1, paramValues[i]);
        }
    }

    private void bindParameter(PreparedStatement statement, int index, Object value) throws SQLException {
        statement.setObject(index, ReportJdbcValueConverter.toJdbcParameter(value));
    }

    private void closeQuietly() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ignored) {
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException ignored) {
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
        resultSet = null;
        preparedStatement = null;
        connection = null;
    }
}
