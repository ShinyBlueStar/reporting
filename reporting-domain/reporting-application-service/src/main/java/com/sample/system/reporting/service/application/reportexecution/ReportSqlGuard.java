package com.sample.system.reporting.service.application.reportexecution;

import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates report SQL before it is stored or executed. The statement is parsed (not pattern matched on
 * its prefix) and only a single plain {@code SELECT}/{@code WITH ... SELECT} is accepted. Locking reads,
 * {@code SELECT ... INTO}, database links and privileged Oracle packages are rejected.
 * <p>
 * This is defence in depth: the reporting database account must still be read-only.
 */
@Component
public class ReportSqlGuard {

    /** Oracle packages/objects that read files, call the network or run dynamic SQL. */
    private static final Pattern FORBIDDEN_TOKENS = Pattern.compile(
            "\\b(DBMS_[A-Z0-9_]*|UTL_[A-Z0-9_]*|SYS\\.[A-Z0-9_$]+|SYS_CONTEXT|XMLTYPE|HTTPURITYPE|"
                    + "SYS_XMLGEN|EXECUTE|OWA_[A-Z0-9_]*|CTXSYS\\.[A-Z0-9_]+)\\b");

    private static final Pattern FOR_UPDATE = Pattern.compile("\\bFOR\\s+UPDATE\\b");

    public void ensureSelectableQuery(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_SQL_QUERY_REQUIRED.getCode(),
                    ErrorCode.REPORT_SQL_QUERY_REQUIRED.name());
        }
        if (sql.contains(";")) {
            throw notAllowed("Multiple SQL statements are not allowed");
        }
        String searchable = stripCommentsAndLiterals(sql).toUpperCase();
        if (FORBIDDEN_TOKENS.matcher(searchable).find()) {
            throw notAllowed("SQL uses a forbidden database object or function");
        }
        if (searchable.contains("@")) {
            throw notAllowed("Database links are not allowed");
        }

        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException ex) {
            throw notAllowed("SQL cannot be parsed as a single SELECT statement");
        }
        if (!(statement instanceof Select select)) {
            throw notAllowed(ErrorCode.REPORT_SQL_QUERY_NOT_ALLOWED.name());
        }
        if (FOR_UPDATE.matcher(searchable).find()) {
            throw notAllowed("Locking reads (FOR UPDATE) are not allowed");
        }
        if (containsIntoClause(select)) {
            throw notAllowed("SELECT ... INTO is not allowed");
        }
        List<String> tables = new TablesNamesFinder<Void>().getTables((Statement) select).stream().toList();
        if (tables.stream().anyMatch(t -> t.contains("@"))) {
            throw notAllowed("Database links are not allowed");
        }
    }

    private boolean containsIntoClause(Select select) {
        return select.getPlainSelect() != null && select.getPlainSelect().getIntoTables() != null;
    }

    private ReportingDomainException notAllowed(String message) {
        return new ReportingDomainException(ErrorCode.REPORT_SQL_QUERY_NOT_ALLOWED.getCode(), message);
    }

    /** Removes comments and quoted literals so keywords inside text cannot trigger or hide matches. */
    static String stripCommentsAndLiterals(String sql) {
        return sql.replaceAll("(?s)/\\*.*?\\*/", " ")
                .replaceAll("--[^\\n]*", " ")
                .replaceAll("'(?:[^']|'')*'", "''");
    }
}
