package com.sample.system.reporting.service.dataaccess.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportFileCleanupJobTest {

    @TempDir
    Path storagePath;

    @Test
    void deletesReportsFromTwoCalendarDaysAgoRegardlessOfTheirTime() throws IOException {
        Path executionDirectory = Files.createDirectories(storagePath.resolve("report-executions/10"));
        Path earlyExpiredReport = Files.writeString(executionDirectory.resolve("early-expired.xlsx"), "expired");
        Path lateExpiredReport = Files.writeString(executionDirectory.resolve("late-expired.xlsx"), "expired");
        Path retainedReport = Files.writeString(executionDirectory.resolve("retained.xlsx"), "retained");
        Path unrelatedFile = Files.writeString(storagePath.resolve("keep.txt"), "unrelated");

        Files.setLastModifiedTime(earlyExpiredReport, FileTime.from(Instant.parse("2026-08-15T20:30:00Z")));
        Files.setLastModifiedTime(lateExpiredReport, FileTime.from(Instant.parse("2026-08-16T20:29:00Z")));
        Files.setLastModifiedTime(retainedReport, FileTime.from(Instant.parse("2026-08-16T20:30:00Z")));
        Files.setLastModifiedTime(unrelatedFile, FileTime.from(Instant.parse("2026-08-10T00:00:00Z")));

        ReportFileCleanupJob job = new ReportFileCleanupJob(storagePath.toString(), 2, "Asia/Tehran");

        int deletedFiles = job.deleteFilesOnOrBefore(LocalDate.of(2026, 8, 16));

        assertEquals(2, deletedFiles);
        assertFalse(Files.exists(earlyExpiredReport));
        assertFalse(Files.exists(lateExpiredReport));
        assertTrue(Files.exists(retainedReport));
        assertTrue(Files.exists(unrelatedFile));
    }

    @Test
    void removesExecutionDirectoryAfterItsExpiredFilesAreDeleted() throws IOException {
        Path executionDirectory = Files.createDirectories(storagePath.resolve("report-executions/11"));
        Path expiredReport = Files.writeString(executionDirectory.resolve("expired.csv"), "expired");
        Files.setLastModifiedTime(expiredReport, FileTime.from(Instant.parse("2026-08-16T12:00:00Z")));

        ReportFileCleanupJob job = new ReportFileCleanupJob(storagePath.toString(), 2, "Asia/Tehran");

        job.deleteFilesOnOrBefore(LocalDate.of(2026, 8, 16));

        assertFalse(Files.exists(executionDirectory));
        assertTrue(Files.isDirectory(storagePath.resolve("report-executions")));
    }
}
