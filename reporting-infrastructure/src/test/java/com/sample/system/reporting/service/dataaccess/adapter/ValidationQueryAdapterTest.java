package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ValidationQueryAdapterTest {

    private final NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
    private final ValidationQueryAdapter adapter = new ValidationQueryAdapter(jdbc, new ReportSqlGuard());

    private void returning(Object value, boolean hasRow) throws Exception {
        doAnswer(invocation -> {
            ResultSet rs = mock(ResultSet.class);
            when(rs.next()).thenReturn(hasRow);
            when(rs.getObject(1)).thenReturn(value);
            invocation.<RowCallbackHandler>getArgument(2).processRow(rs);
            return null;
        }).when(jdbc).query(anyString(), any(Map.class), any(RowCallbackHandler.class));
    }

    @Test
    void returnsNumericCountAndZeroWhenNoRow() throws Exception {
        returning(7, true);
        assertEquals(7L, adapter.executeCount("select count(*) from t", Map.of()));
        returning(null, false);
        assertEquals(0L, adapter.executeCount("select count(*) from t", Map.of()));
    }

    @Test
    void parsesTextCounts() throws Exception {
        returning("12", true);
        assertEquals(12L, adapter.executeCount("select count(*) from t", Map.of()));
    }

    @Test
    void nonSelectSqlNeverReachesTheDatabase() {
        assertThrows(ReportingDomainException.class, () -> adapter.executeCount("delete from t", Map.of()));
        verifyNoInteractions(jdbc);
    }

    @Test
    void databaseFailureIsWrappedWithoutLeakingDetails() {
        doThrow(new DataAccessResourceFailureException("ORA-00942 secret_table"))
                .when(jdbc).query(anyString(), any(Map.class), any(RowCallbackHandler.class));
        ReportingDomainException ex = assertThrows(ReportingDomainException.class,
                () -> adapter.executeCount("select count(*) from t", Map.of()));
        assertFalse(ex.getMessage().contains("secret_table"));
    }
}
