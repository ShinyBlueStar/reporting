package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.JoinColumn;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusLookupForeignKeyOwnershipTest {

    @Test
    void installmentStatusForeignKeyIsOwnedByDatabaseMigration() throws NoSuchFieldException {
        assertHibernateDoesNotGenerateForeignKey(
                InstallmentReportEntityQuery.class.getDeclaredField("installmentStatusLookup"));
    }

    @Test
    void loanStatusForeignKeyIsOwnedByDatabaseMigration() throws NoSuchFieldException {
        assertHibernateDoesNotGenerateForeignKey(
                LoanReportEntityQuery.class.getDeclaredField("loanStatusLookup"));
    }

    private void assertHibernateDoesNotGenerateForeignKey(Field association) {
        JoinColumn joinColumn = association.getAnnotation(JoinColumn.class);
        assertEquals(ConstraintMode.NO_CONSTRAINT, joinColumn.foreignKey().value());
    }
}
