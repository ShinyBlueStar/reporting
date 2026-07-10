package com.sample.system.reporting.service.application.reportexecution;

import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportSqlGuardTest {

    private final ReportSqlGuard guard = new ReportSqlGuard();

    private void rejected(String sql) {
        assertThrows(ReportingDomainException.class, () -> guard.ensureSelectableQuery(sql), sql);
    }

    @Test
    void allowsSelectWithAndOracleSyntax() {
        assertDoesNotThrow(() -> guard.ensureSelectableQuery("  select * from t where a = :a"));
        assertDoesNotThrow(() -> guard.ensureSelectableQuery("WITH x AS (SELECT 1 AS n FROM dual) SELECT n FROM x"));
        assertDoesNotThrow(() -> guard.ensureSelectableQuery(
                "SELECT l.id, MAX(i.status_title_fa) KEEP (DENSE_RANK LAST ORDER BY NVL(i.display_order, 0)) AS s "
                        + "FROM report_loan l LEFT JOIN installment_status_lookup i ON i.status_code = l.loan_status "
                        + "WHERE l.creation_date >= :fromDate GROUP BY l.id"));
        assertDoesNotThrow(() -> guard.ensureSelectableQuery("SELECT 'a;b' AS x FROM dual".replace(";", "")));
    }

    @Test
    void rejectsBlankAndNull() {
        rejected(null);
        rejected("   ");
    }

    @Test
    void rejectsNonSelectStatements() {
        for (String sql : new String[]{"DELETE FROM t", "update t set a=1", "DROP TABLE t", "BEGIN null; END",
                "INSERT INTO t VALUES (1)", "CALL proc()", "MERGE INTO t USING s ON (1=1) WHEN MATCHED THEN UPDATE SET a=1"}) {
            rejected(sql);
        }
    }

    @Test
    void rejectsStatementChainingAndLockingAndInto() {
        rejected("SELECT 1 FROM dual; DROP TABLE t");
        rejected("SELECT * FROM t FOR UPDATE");
        rejected("SELECT * INTO backup_t FROM t");
    }

    @Test
    void rejectsDatabaseLinksAndPrivilegedPackages() {
        rejected("SELECT * FROM remote_table@other_db");
        rejected("SELECT UTL_HTTP.REQUEST('http://x') FROM dual");
        rejected("SELECT dbms_xmlgen.getxml('select 1 from dual') FROM dual");
        rejected("SELECT sys.dbms_random.value FROM dual");
    }

    @Test
    void keywordsInsideLiteralsOrCommentsAreNotRejected() {
        assertDoesNotThrow(() -> guard.ensureSelectableQuery("SELECT 'dbms_output for update' AS note FROM dual"));
        assertDoesNotThrow(() -> guard.ensureSelectableQuery("SELECT 1 AS x FROM dual -- utl_http note"));
    }

    @Test
    void rejectsUnparseableSql() {
        rejected("SELEC garbage ((");
    }
}
