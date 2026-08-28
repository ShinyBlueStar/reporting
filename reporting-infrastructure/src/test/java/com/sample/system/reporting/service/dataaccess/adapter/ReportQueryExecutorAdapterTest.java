package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportQueryExecutorAdapterTest {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final ReportQueryExecutorAdapter adapter = new ReportQueryExecutorAdapter(namedParameterJdbcTemplate);

    @BeforeEach
    void setUp() {
        when(namedParameterJdbcTemplate.getJdbcTemplate()).thenReturn(jdbcTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void executesQueryAndStopsAtMaxRowsPreservingColumnOrder() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("select * from test where status = ?")).thenReturn(statement);

        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("NAME");
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getObject(1)).thenReturn(1L, 2L);
        when(resultSet.getObject(2)).thenReturn("first", "second");

        when(jdbcTemplate.query(any(PreparedStatementCreator.class), any(ResultSetExtractor.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementCreator creator = invocation.getArgument(0);
                    ResultSetExtractor<Object> extractor = invocation.getArgument(1);
                    assertSame(statement, creator.createPreparedStatement(connection));
                    return extractor.extractData(resultSet);
                });

        List<Map<String, Object>> rows = new ArrayList<>();
        adapter.executeQuery(
                "select * from test where status = :status",
                Map.of("status", "ACTIVE"),
                10,
                2,
                rows::add);

        assertEquals(2, rows.size());
        assertEquals(List.of("ID", "NAME"), rows.get(0).keySet().stream().toList());
        assertEquals(1L, rows.get(0).get("ID"));
        assertEquals("first", rows.get(0).get("NAME"));
        assertEquals(2L, rows.get(1).get("ID"));
        assertEquals("second", rows.get(1).get("NAME"));
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet, times(2)).getObject(2);
        verify(connection).setReadOnly(true);
        verify(statement).setQueryTimeout(10);
        verify(statement).setMaxRows(2);
        verify(statement).setObject(1, "ACTIVE");
    }

    @Test
    void executesQueryWithEmptyParametersWhenParametersAreNull() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("select 1")).thenReturn(statement);

        when(jdbcTemplate.query(any(PreparedStatementCreator.class), any(ResultSetExtractor.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementCreator creator = invocation.getArgument(0);
                    assertSame(statement, creator.createPreparedStatement(connection));
                    return null;
                });

        adapter.executeQuery("select 1", null, null, 1, row -> {
        });

        verify(namedParameterJdbcTemplate).getJdbcTemplate();
        verify(jdbcTemplate).query(any(PreparedStatementCreator.class), any(ResultSetExtractor.class));
        verify(statement).setMaxRows(1);
        verify(statement, never()).setObject(anyInt(), any());
    }

    @Test
    void wrapsJdbcFailuresInReportingDomainException() {
        when(jdbcTemplate.query(any(PreparedStatementCreator.class), any(ResultSetExtractor.class)))
                .thenThrow(new DataAccessResourceFailureException("database unavailable"));

        ReportingDomainException exception = assertThrows(
                ReportingDomainException.class,
                () -> adapter.executeQuery("select * from test", Map.of(), 10, 10, row -> {
                }));

        assertTrue(exception.getMessage().contains("database unavailable"));
    }
}
