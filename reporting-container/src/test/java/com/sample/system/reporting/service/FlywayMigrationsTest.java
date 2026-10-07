package com.sample.system.reporting.service;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Flyway is disabled at runtime (spring.flyway.enabled=false) but the scripts must stay runnable.
 * This applies them to an in-memory H2 in Oracle mode and checks the result.
 */
class FlywayMigrationsTest {

    @Test
    void migrationsApplyCleanlyAndSeedLookups() throws Exception {
        org.h2.jdbcx.JdbcDataSource ds = new org.h2.jdbcx.JdbcDataSource();
        ds.setURL("jdbc:h2:mem:flyway_check;MODE=Oracle;DB_CLOSE_DELAY=-1");
        var result = Flyway.configure().dataSource(ds).locations("classpath:db/migration").load().migrate();
        assertEquals(6, result.migrationsExecuted);
        assertTrue(result.success);
        assertEquals(9, count(ds, "loan_status_lookup"));
        assertEquals(8, count(ds, "installment_status_lookup"));
    }

    private int count(DataSource ds, String table) throws Exception {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("select count(*) from " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
